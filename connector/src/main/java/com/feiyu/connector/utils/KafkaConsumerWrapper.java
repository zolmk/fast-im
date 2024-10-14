package com.feiyu.connector.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;

@Slf4j
@Data
public class KafkaConsumerWrapper {
  private final KafkaConsumer<String, byte[]> consumer;
  private final long qid;
  public KafkaConsumerWrapper(final KafkaConsumer<String, byte[]> consumer, final long qid) {
    this.consumer = consumer;
    this.qid = qid;
  }

  public ConsumerRecords<String, byte[]> poll(Duration timeout) {
    return this.consumer.poll(timeout);
  }
}
