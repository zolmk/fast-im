package com.feiyu.connector.tasks;

import com.feiyu.base.RetryableTask;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.utils.MessageFailoverInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class MessageSendTask extends RetryableTask {
  private final Channel channel;
  private final long to;
  private final Messages.Msg msg;
  private final Consumer<MessageFailoverInfo> failoverConsumer;
  public MessageSendTask(int retryCount, int retryInterval, TimeUnit timeUnit, Channel channel, long to,  Messages.Msg msg, Consumer<MessageFailoverInfo> failoverConsumer) {
    super(retryCount, retryInterval, timeUnit, channel.eventLoop());
    this.channel = channel;
    this.to = to;
    this.msg = msg;
    this.failoverConsumer = failoverConsumer;
  }
  @Override
  protected boolean execute() throws Exception {
    if (channel.isActive() && channel.isWritable()) {
      channel.writeAndFlush(msg);
    } else {
      // 通道不可写，直接进行故障转移，然后结束任务
      this.failoverConsumer.accept(new MessageFailoverInfo(String.valueOf(to), msg, MessageFailoverInfo.Reason.SEND_EXHAUSTED));
      return true;
    }
    return false;
  }

  @Override
  public void failover() {
    this.failoverConsumer.accept(new MessageFailoverInfo(String.valueOf(to), msg, MessageFailoverInfo.Reason.SEND_EXHAUSTED));
  }
}