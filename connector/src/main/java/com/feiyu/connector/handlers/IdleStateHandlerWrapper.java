package com.feiyu.connector.handlers;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * IdleStateHandler的包装类
 *
 * @author Zhuff
 */
public class IdleStateHandlerWrapper extends IdleStateHandler implements BaseHandler{
  public IdleStateHandlerWrapper(Properties properties) {
    super(false,
      Integer.parseInt(properties.getProperty("client.reader-idle-time", "0")),
      Integer.parseInt(properties.getProperty("client.writer-idle-time", "0")),
      Integer.parseInt(properties.getProperty("client.all-idle-time", "0")),
      TimeUnit.valueOf(properties.getProperty("client.timeunit", "MILLISECONDS")));
  }

  @Override
  public String name() {
    return IdleStateHandlerWrapper.class.getSimpleName();
  }
}
