package com.feiyu.connector.config.mq;

import com.feiyu.base.config.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "connector.kafka")
public class KafkaConfig extends KafkaProperties {
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
