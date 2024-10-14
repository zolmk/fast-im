package com.feiyu.connector.service.impl;

import com.feiyu.base.QueueInfo;
import com.feiyu.connector.service.MessageReceiver;
import io.netty.channel.Channel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class AbstractMessageConsumer implements MessageReceiver {

  protected Map<Long, Map<String, Channel>> mqMap = new ConcurrentHashMap<>();

  public abstract void mount(List<QueueInfo> mqs);

  public abstract void unmount(List<QueueInfo> mqs);

  @Override
  public void register(String uid, Channel channel, long mqId) {
    if (!mqMap.containsKey(mqId)) {
      log.error("Register Queue Id: {} not exist", mqId);
      throw new RuntimeException("Queue Id: " + mqId + " not exist");
    }
    mqMap.get(mqId).put(uid, channel);
  }

  @Override
  public void unregister(String uid, long mq) {
    if (!mqMap.containsKey(mq)) {
      log.info("Unregister Queue Id: {} not exist", mq);
      return;
    }
    mqMap.get(mq).remove(uid);
  }

  public abstract String name();

  protected static class MessageConsumerThreadFactory implements ThreadFactory {
    private final AtomicInteger cnt = new AtomicInteger(1);
    @Override
    public Thread newThread(@NonNull Runnable r) {
      Thread t = new Thread(r);
      t.setDaemon(true);
      t.setName(name());
      return t;
    }
    private String name() {
      return "message-consumer-thread-" + cnt.getAndIncrement();
    }
  }
}
