package com.feiyu.base.config;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Setter
@Getter
public abstract class KafkaProperties {
  protected String acks = "all";
  protected int retries = 3;
  protected int batchSize = 128;
  protected int lingerMs = 20;
  protected String consumerGroup = "connector";
  protected Duration timeout = Duration.ofMillis(200);
  protected int preEventLoopConsumerCnt = 10;
  protected int partitionNumber = 2;
  protected int replicasNumber = 1;
}
