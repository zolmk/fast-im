package com.feiyu.connector.service.impl;

import com.feiyu.base.QueueInfo;
import com.feiyu.base.RetryableTask;
import com.feiyu.base.RunOnce;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.config.mq.KafkaConfig;
import com.feiyu.connector.handlers.ControlMsgHandler;
import com.feiyu.connector.handlers.NoticeMsgHandler;
import com.feiyu.connector.service.NoticeHandleService;
import com.feiyu.connector.tasks.MessageSendTask;
import com.feiyu.connector.utils.KafkaConsumerWrapper;
import com.feiyu.connector.utils.KafkaConsumersFactory;
import com.feiyu.connector.utils.MessageFailoverInfo;
import com.feiyu.connector.utils.SimpleEventLoop;
import com.feiyu.interfaces.idl.IMessageHandleService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 基于Kafka的消息接收器
 * 消息接收器消费队列中的消息，并将消息直接发送到用户Channel中
 */
@Slf4j
@Service(value = "kafkaMessageReceiver")
@ConditionalOnProperty(name = "connector.message-consumer", havingValue = "kafka", matchIfMissing = true)
public class KafkaMessageConsumer extends AbstractMessageConsumer implements Consumer<MessageFailoverInfo> {
  private final KafkaConfig kafkaConfig;
  // 保存 queue id 到EventLoop的映射
  private final Map<Long, MessageConsumerTask> queueTasks = new ConcurrentHashMap<>();

  private final Consumer<MessageFailoverInfo> failoverConsumer;

  private final ThreadFactory threadFactory;

  private final IMessageHandleService messageHandleService;

  public KafkaMessageConsumer(KafkaConfig kafkaConfig, IMessageHandleService messageHandleService) {
    this.kafkaConfig = kafkaConfig;
    this.messageHandleService = messageHandleService;
    this.failoverConsumer = this;
    this.threadFactory = new MessageConsumerThreadFactory();
  }

  @Override
  public synchronized void mount(List<QueueInfo> mqs) {
    log.info("mount: {}", mqs);
    if (mqs.isEmpty()) {return;}
    for (QueueInfo mq : mqs) {
      mqMap.put(mq.getId(), new ConcurrentHashMap<>());
    }
    // 每个eventloop 负责消费多个queue
    List<KafkaConsumerWrapper> consumers = KafkaConsumersFactory.create(mqs, kafkaConfig);
    int preEventLoopConsumerCnt = kafkaConfig.getPreEventLoopConsumerCnt();
    for (int i = 0; i < consumers.size(); i+=preEventLoopConsumerCnt) {
      List<KafkaConsumerWrapper> subList = consumers.subList(i, Math.min(i + preEventLoopConsumerCnt, consumers.size()));
      SimpleEventLoop eventLoop = new SimpleEventLoop();
      for (KafkaConsumerWrapper consumer : subList) {
        MessageConsumerTask messageConsumerTask = new MessageConsumerTask(consumer, failoverConsumer);
        eventLoop.add(messageConsumerTask);
        queueTasks.put(consumer.getQid(), messageConsumerTask);
      }
      this.threadFactory.newThread(eventLoop).start();
    }
  }

  @Override
  public synchronized void unmount(List<QueueInfo> mqs) {
    log.info("unmount: {}", mqs);
    for (QueueInfo mq : mqs) {
      Map<String, Channel> remove = mqMap.remove(mq.getId());
      // 关闭mq上绑定的用户Channel
      for (Channel channel : remove.values()) {
        if (channel.isOpen()) {
          channel.writeAndFlush(Messages.ControlMsg.newBuilder().setType(Messages.ControlType.CONNECT_REST).setMsgId(-1L).build())
            .addListener((ChannelFutureListener) future -> {
              // ignore result
              future.channel().close();
            });
        }
      }
    }
    for (QueueInfo mq : mqs) {
      if (queueTasks.containsKey(mq.getId())) {
        queueTasks.get(mq.getId()).revoke();
      }
    }
  }

  @Override
  public String name() {
    return "kafka";
  }

  @Override
  public void accept(MessageFailoverInfo messageFailoverInfo) {
    switch (messageFailoverInfo.getReason()) {
      case CLIENT_CLOSED: {
        log.info("client {} closed", messageFailoverInfo.getTo());
      } break;
      case QUEUE_UNMOUNT: {

      } break;
      default: {
        log.info("unknown reason: {}", messageFailoverInfo.getReason());
      }
    }
  }

  class MessageConsumerTask implements RunOnce {
    private final KafkaConsumerWrapper kafkaConsumerWrapper;
    private volatile boolean running = true;
    private final Duration duration;
    private final Consumer<MessageFailoverInfo> failoverConsumer;
    private final long qid;
    MessageConsumerTask(KafkaConsumerWrapper consumer, Consumer<MessageFailoverInfo> failoverConsumer) {
      this.kafkaConsumerWrapper = consumer;
      this.duration = kafkaConfig.getTimeout();
      this.failoverConsumer = failoverConsumer;
      this.qid = consumer.getQid();
    }

    @Override
    public void runOnce() throws Exception {
      ConsumerRecords<String, byte[]> poll = kafkaConsumerWrapper.poll(duration);
      if (poll == null || poll.isEmpty()) {return;}
      // 将消息分发到每一个Channel
      for (ConsumerRecord<String, byte[]> record : poll) {
        Messages.Msg msg = Messages.Msg.parseFrom(record.value());
        String toUid = record.key();
        Map<String, Channel> channelMap = null;
        MessageFailoverInfo.Reason reason = MessageFailoverInfo.Reason.QUEUE_UNMOUNT;
        if (mqMap.containsKey(qid)) {
          channelMap = mqMap.get(qid);
          Channel channel = channelMap.get(toUid);
          if (channel != null && channel.isOpen()) {
            if (Messages.MsgType.GENERIC.equals(msg.getType())) {
              // 聊天消息
              Messages.GenericMsg genericMsg = msg.getGenericMsg();
              Messages.MsgExtraInfo extraInfo = genericMsg.getExtraInfo();
              ChannelPipeline pipeline = channel.pipeline();
              long to = pipeline.get(ControlMsgHandler.class).getClientInfo().getUid();
              MessageSendTask messageSendTask = new MessageSendTask(3, 1, TimeUnit.SECONDS, channel, to, msg, this.failoverConsumer);
              channel.eventLoop()
                .schedule( messageSendTask, 0, TimeUnit.MILLISECONDS);
              NoticeHandleService noticeHandleService = pipeline.get(NoticeMsgHandler.class).getNoticeHandleService();

              noticeHandleService.registerRevocableTask(extraInfo.getMsgId(), messageSendTask);
            } else {
              channel.writeAndFlush(msg);
            }
            continue;
          }
          reason = MessageFailoverInfo.Reason.CLIENT_CLOSED;
        }
        // failed
        failoverConsumer.accept(new MessageFailoverInfo(toUid, msg, reason));
      }
    }

    @Override
    public void revoke() {
      running = false;
    }

    @Override
    public boolean isRevoked() {
      return !running;
    }
  }

}
