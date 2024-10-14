package com.feiyu.connector.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnifiedExceptionHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ControlMsgHandler controlMsgHandler = ctx.pipeline().get(ControlMsgHandler.class);
    log.error("client {} occur exception {}.", controlMsgHandler.getClientInfo().getUid(), cause.getMessage());
  }
}
