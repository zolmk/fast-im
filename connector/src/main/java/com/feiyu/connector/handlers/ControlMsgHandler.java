package com.feiyu.connector.handlers;

import com.feiyu.base.eventbus.EventBus;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.service.MQChooser;
import com.feiyu.connector.utils.ChannelRegisterEvent;
import com.feiyu.connector.utils.ChannelUnregisterEvent;
import com.feiyu.connector.utils.NamedBeanProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 控制消息处理器.
 *
 * @author Zhuff
 */
@Slf4j
public class ControlMsgHandler extends SimpleChannelInboundHandler<Messages.Msg> implements NamedHandler {
  private final MQChooser chooser;
  public ControlMsgHandler() {
    this.chooser = NamedBeanProvider.getSingleton(MQChooser.class);
  }
  private Messages.ClientInfo clientInfo;
  private long qid;

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, Messages.Msg msg) throws Exception {
    if (Messages.MsgType.CONTROL.equals(msg.getType())) {
      Messages.ControlMsg controlMsg = msg.getControlMsg();
      switch (controlMsg.getType()) {
        case CLIENT_LOGIN: {
          // 用户登录
          this.clientInfo = controlMsg.getClientInfo();
          this.qid = chooser.choice(clientInfo);
          EventBus.post(new ChannelRegisterEvent(String.valueOf(clientInfo.getUid()), channelHandlerContext.channel(), qid));
        } break;
        case MSG_DELIVERED_FAIL: {
          // ignore
        } break;
        default: {
          log.info("unknown msg type: {}", msg.getType());
        } break;

      }
    }
    channelHandlerContext.fireChannelRead(msg);
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    if (clientInfo != null) {
      EventBus.post(new ChannelUnregisterEvent(String. valueOf(clientInfo.getUid()), qid));
    }
    super.channelUnregistered(ctx);
  }

  @Override
  public String name() {
    return "controlMsgHandler";
  }
}
