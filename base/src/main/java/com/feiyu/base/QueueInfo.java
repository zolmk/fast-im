package com.feiyu.base;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class QueueInfo {

  private long id;

  private String queueName;

  private String connectStr;

  private final static AtomicLong idGenerator = new AtomicLong(0);

  // for 反序列化
  public QueueInfo() {
  }

  QueueInfo(String connectStr, String queueName) {
    this.queueName = queueName;
    this.connectStr = connectStr;
    this.id = idGenerator.incrementAndGet();
  }
}
