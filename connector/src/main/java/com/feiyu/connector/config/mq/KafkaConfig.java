package com.feiyu.connector.config.mq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "connector.kafka")
@Setter
@Getter
public class KafkaConfig {
  private String acks = "all";
  private int retries = 3;
  private int batchSize = 128;
  private int lingerMs = 20;
  private String consumerGroup = "connector";
  private Duration timeout = Duration.ofMillis(200);
  private int preEventLoopConsumerCnt = 10;
  private int partitionNumber = 2;
  private int replicasNumber = 1;

  @Bean
  public Properties kafkaConsuemrProperties() {
    Properties props = new Properties();
    props.put("acks", acks);
    props.put("retries", retries);
    props.put("batch.size", batchSize);
    props.put("linger.ms", lingerMs);
    props.put("group.id", consumerGroup);
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    return props;
  }
}
