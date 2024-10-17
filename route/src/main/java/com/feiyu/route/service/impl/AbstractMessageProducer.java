package com.feiyu.route.service.impl;

import com.feiyu.base.proto.Messages;
import com.feiyu.route.service.MessageProducer;
import com.feiyu.route.util.Producer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMessageProducer implements MessageProducer {
  private final static Map<Long, Producer> producers = new ConcurrentHashMap<>();
  @Override
  public void produce(long to, Messages.Msg msg) throws Exception {
    Producer producer = null;
    if ((producer = producers.get(to)) != null && producer.isAlive()) {
      producer.produce(to, msg);
      return;
    }
    producer = createProducer(to);
    producer.produce(to, msg);
    producers.put(to, producer);
  }

  /**
   * 创建Producer，需要保证去做同步，保证Producer的个数
   * @param to
   * @return
   */
  protected abstract Producer createProducer(long to) throws Exception;


  @Override
  public void close() throws IOException {
    for (Producer producer : producers.values()) {
      if (producer.isAlive()) {
        producer.close();
      }
    }
  }
}
