package com.feiyu.connector.service.impl;

import com.feiyu.base.QueueInfo;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.service.MessageReceiver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于Kafka的消息接收器
 * 消息接收器消费队列中的消息，并将消息直接发送到用户Channel中
 */
@Slf4j
@Service(value = "kafkaMessageReceiver")
@ConditionalOnProperty(name = "connector.message-receiver", havingValue = "kafka", matchIfMissing = true)
public class KafkaMessageReceiver implements MessageReceiver {
  private Map<Long, Map<String, Channel>> mqMap = new ConcurrentHashMap<>();


  @Override
  public synchronized void mount(List<QueueInfo> mqs) {
    log.info("mount: {}", mqs);
    for (QueueInfo mq : mqs) {
      mqMap.put(mq.getId(), new ConcurrentHashMap<>());
    }
    // TODO 订阅Topic

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
    // TODO cancel Topic
  }

  @Override
  public void register(String uid, Channel channel, long mqId) {
    if (!mqMap.containsKey(mqId)) {
      log.error("Queue Id: {} not exist", mqId);
      throw new RuntimeException("Queue Id: " + mqId + " not exist");
    }
    mqMap.get(mqId).put(uid, channel);
  }

  @Override
  public void unregister(String uid, long mq) {
    if (!mqMap.containsKey(mq)) {
      log.info("Queue Id: {} not exist", mq);
      return;
    }
    mqMap.get(mq).remove(uid);
  }

  @Override
  public String name() {
    return "kafka";
  }
}
