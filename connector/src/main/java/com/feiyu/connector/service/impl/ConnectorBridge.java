package com.feiyu.connector.service.impl;


import com.fasterxml.jackson.databind.json.JsonMapper;
import com.feiyu.base.QueueInfo;
import com.feiyu.base.QueueInfoStore;
import com.feiyu.base.eventbus.EventBus;
import com.feiyu.base.eventbus.Subscribe;
import com.feiyu.connector.service.MessageConsumer;
import com.feiyu.connector.utils.ChannelRegisterEvent;
import com.feiyu.connector.utils.ChannelUnregisterEvent;
import com.feiyu.connector.utils.NamedBeanProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.feiyu.base.Constants.*;

/**
 * 连接器桥接类
 * 用来解耦合
 */
@Slf4j
@Component
@ConditionalOnClass(value = MessageConsumer.class)
public class ConnectorBridge {

  @Resource
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private JsonMapper jsonMapper;

  public ConnectorBridge() {
    EventBus.register(this);
  }

  @Subscribe(async = true)
  public void handlerRegister(ChannelRegisterEvent event) {
    log.info("ConnectorBridge receive register event.");
    writeQueueInfoToCache(event);
    MessageConsumer messageConsumer = NamedBeanProvider.getSingleton(MessageConsumer.class);
    messageConsumer.register(event.getUid(), event.getChannel(), event.getMq());
  }

  @Subscribe(async = true)
  public void handlerUnregister(ChannelUnregisterEvent event) {
    log.info("ConnectorBridge receive unregister event.");
    MessageConsumer messageConsumer = NamedBeanProvider.getSingleton(MessageConsumer.class);
    messageConsumer.unregister(event.getUid(), event.getMq());
  }


  private void writeQueueInfoToCache(ChannelRegisterEvent event) {
    try {
      QueueInfo queueInfo = QueueInfoStore.get(event.getMq());
      this.redisTemplate.opsForValue().set(QUEUE_INFO_PREFIX + event.getUid(), jsonMapper.writeValueAsString(queueInfo));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
