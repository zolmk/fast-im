package com.feiyu.connector.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 基础的ChannelHandler.
 *
 * @author Zhuff
 */
public interface BaseHandler extends ChannelHandler {
  /**
   * 获取Handler的名字.
   *
   * @return name
   */
  String name();
}
