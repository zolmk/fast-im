package com.feiyu.route.config;

import com.feiyu.base.config.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration(proxyBeanMethods = true)
@ConfigurationProperties("kafka")
public class KafkaConfig extends KafkaProperties {

  @Bean
  public Properties kafkaProducerProperties() {
    Properties props = new Properties();
    props.put("acks", "all");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
    return props;
  }
}
