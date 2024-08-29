package com.feiyu.connector.handlers;

import com.feiyu.base.proto.Messages;
import com.feiyu.interfaces.IMessageHandleService;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * 通用消息处理器.
 *
 * @author Zhuff
 */
@Slf4j
public class GenericMsgHandler extends SimpleChannelInboundHandler<Messages.Msg> {
  @DubboReference
  private IMessageHandleService messageHandleService;
  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Messages.Msg msg) throws Exception {
    if (Messages.MsgType.GENERIC.equals(msg.getType())) {
      if (log.isInfoEnabled()) {
        log.info("received msg : {}", msg);
      }
      messageHandleService.handle(msg);
      return;
    }
    channelHandlerContext.fireChannelRead(msg);
  }
}
