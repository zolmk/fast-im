package com.feiyu.route.util;

import com.feiyu.base.QueueInfo;
import com.feiyu.base.proto.Messages;
import com.feiyu.route.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class KafkaProducerWrapper implements Producer {
  private final KafkaProducer<String, byte[]> producer;
  private final String topic;
  private final QueueInfo queueInfo;
  private volatile boolean closed = false;
  public KafkaProducerWrapper(QueueInfo queueInfo, KafkaConfig kafkaConfig) {
    Properties props = new Properties();
    props.put("bootstrap.servers", queueInfo.getConnectStr());
    props.putAll(kafkaConfig.kafkaProducerProperties());
    producer = new KafkaProducer<>(props);
    this.topic = queueInfo.getQueueName();
    this.queueInfo = queueInfo;
  }

  @Override
  public void produce(long to, Messages.Msg msg) {
    try {
      log.info("to: {}, msg: {}, topic: {}", to, msg, topic);
      producer.send(new ProducerRecord<>(topic, String.valueOf(to), msg.toByteArray()));
    } catch (Exception e) {
      log.error("kafka producer send error", e);
      try {
        closed = true;
        close();
      } catch (IOException ignore) {
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean isAlive() {
    return !closed;
  }

  @Override
  public void close() throws IOException {
    producer.close();
  }
}
