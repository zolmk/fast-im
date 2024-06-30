package com.feiyu.connector.handlers;

import com.feiyu.base.proto.Messages;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 控制消息处理器.
 *
 * @author Zhuff
 */
public class ControlMsgHandler extends SimpleChannelInboundHandler<Messages.Msg> {
  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Messages.Msg msg) throws Exception {
    if (Messages.MsgType.CONTROL.equals(msg.getType())) {

      return;
    }
    channelHandlerContext.fireChannelRead(msg);
  }
}
