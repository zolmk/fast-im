package com.feiyu.connector.service.impl;

import com.feiyu.connector.service.MessageReceiver;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于Kafka的消息接收器
 */
@Slf4j
@Lazy
@Component(value = "kafkaMessageReceiver")
public class KafkaMessageReceiver implements MessageReceiver {


  @Override
  public void mount(List<String> mqs) {
    log.info("mount: {}", mqs);
  }

  @Override
  public void unmount(List<String> mqs) {
    log.info("unmount: {}", mqs);
  }

  @Override
  public void register(String uid, Channel channel, String mq) {

  }

  @Override
  public void unregister(String uid, String mq) {

  }

  @Override
  public String name() {
    return "kafka";
  }
}
