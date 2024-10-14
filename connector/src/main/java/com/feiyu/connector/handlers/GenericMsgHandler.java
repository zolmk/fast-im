package com.feiyu.connector.handlers;

import com.feiyu.base.proto.ProtocolUtil;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.service.impl.MessageHandleServiceWrapper;
import com.feiyu.connector.utils.NamedBeanProvider;
import com.feiyu.interfaces.idl.IMessageHandleService;
import com.feiyu.interfaces.idl.MsgHandleRsp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 通用消息处理器.
 *
 * @author Zhuff
 */
@Slf4j
public class GenericMsgHandler extends SimpleChannelInboundHandler<Messages.Msg> implements NamedHandler {
  // 客户端级别的消息序列号，用来做消息去重
  @Setter
  private long cltMsgSeq = 0L;

  private final IMessageHandleService messageHandleService;
  public GenericMsgHandler() {
    this.messageHandleService = NamedBeanProvider.getSingleton(MessageHandleServiceWrapper.class);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Messages.Msg msg) throws Exception {
    if (Messages.MsgType.GENERIC.equals(msg.getType())) {
      handleGenericMsg(ctx, msg);
      return;
    }
    ctx.fireChannelRead(msg);
  }

  @Override
  public String name() {
    return "genericMsgHandler";
  }

  private void handleGenericMsg(ChannelHandlerContext ctx, Messages.Msg msg) {
    Messages.GenericMsg genericMsg = msg.getGenericMsg();
    if (genericMsg.getCltSeq() <= cltMsgSeq) {
      // 重复的消息，发送 challenge ack
      log.info("Expired message received. Send challenge ack.");
      ctx.writeAndFlush(ProtocolUtil.messageRevAck(-1, cltMsgSeq + 1));
      return;
    }
    log.info("received msg : {}", msg);
    this.cltMsgSeq = genericMsg.getCltSeq() + 1;
    MsgHandleRsp msgHandleRsp = messageHandleService.handle(msg);
    // 发送消息已接收确认
    ctx.writeAndFlush(ProtocolUtil.messageRevAck(msgHandleRsp.getMsgId(), cltMsgSeq));
  }
}
