package com.feiyu.connector.handlers;

import com.feiyu.base.Revocable;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.service.NoticeHandleService;
import com.feiyu.connector.service.impl.SimpleNoticeHandleService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * 通知消息处理器
 */
@Slf4j
public class NoticeMsgHandler extends SimpleChannelInboundHandler<Messages.Msg> implements NamedHandler {
  @Getter
  private final NoticeHandleService noticeHandleService;

  public NoticeMsgHandler() {
    this.noticeHandleService = new SimpleNoticeHandleService();
  }

  @Override
  public String name() {
    return "noticeMsgHandler";
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Messages.Msg msg) throws Exception {
    if (Messages.MsgType.NOTICE.equals(msg.getType())) {
      Messages.NoticeMsg noticeMsg = msg.getNoticeMsg();
      switch (noticeMsg.getType()) {
        case DELIVERED_ACK: {
          noticeHandleService.handleDeliveryAck(noticeMsg);
        } break;
        case READ_ACK: {
          // 消息已读 ack。客户端
          noticeHandleService.handleReadAck(noticeMsg);
        } break;
      }
      return;
    }
    ctx.fireChannelRead(msg);
  }

}
