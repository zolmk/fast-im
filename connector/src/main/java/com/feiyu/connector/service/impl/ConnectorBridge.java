package com.feiyu.connector.service.impl;


import com.feiyu.base.eventbus.EventBus;
import com.feiyu.base.eventbus.Subscribe;
import com.feiyu.connector.service.MessageReceiver;
import com.feiyu.connector.utils.ChannelRegisterEvent;
import com.feiyu.connector.utils.ChannelUnregisterEvent;
import com.feiyu.connector.utils.NamedBeanProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 连接器桥接类
 * 用来解耦合
 */
@Slf4j
@Component
@ConditionalOnClass(value = MessageReceiver.class)
public class ConnectorBridge {

  public ConnectorBridge() {
    EventBus.register(this);
  }

  @Subscribe(async = true)
  public void handlerRegister(ChannelRegisterEvent event) {
    log.info("ConnectorBridge receive register event.");
    MessageReceiver messageReceiver = NamedBeanProvider.getSingleton(MessageReceiver.class);
    messageReceiver.register(event.getUid(), event.getChannel(), event.getMq());
  }

  @Subscribe(async = true)
  public void handlerUnregister(ChannelUnregisterEvent event) {
    log.info("ConnectorBridge receive unregister event.");
    MessageReceiver messageReceiver = NamedBeanProvider.getSingleton(MessageReceiver.class);
    messageReceiver.unregister(event.getUid(), event.getMq());
  }

}
