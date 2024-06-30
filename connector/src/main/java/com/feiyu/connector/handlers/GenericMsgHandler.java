package com.feiyu.connector.handlers;

import com.feiyu.base.proto.Messages;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用消息处理器.
 *
 * @author Zhuff
 */
@Slf4j
public class GenericMsgHandler extends SimpleChannelInboundHandler<Messages.Msg> {
  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Messages.Msg msg) throws Exception {
    if (Messages.MsgType.GENERIC.equals(msg.getType())) {
      if (log.isInfoEnabled()) {
        log.info("received msg : {}", msg);
      }
      return;
    }
    channelHandlerContext.fireChannelRead(msg);
  }
}
