package com.feiyu.base;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

public class QueueInfo {

  @Getter
  private long id;

  @Getter
  @Setter
  private String queueName;

  @Getter
  @Setter
  private String connectStr;

  private final static AtomicLong idGenerator = new AtomicLong(0);

  QueueInfo(String connectStr, String queueName) {
    this.queueName = queueName;
    this.connectStr = connectStr;
    this.id = idGenerator.incrementAndGet();
  }
}
