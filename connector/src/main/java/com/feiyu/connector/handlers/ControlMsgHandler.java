package com.feiyu.connector.handlers;

import com.feiyu.base.QueueInfoStore;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.service.ClientLoginService;
import com.feiyu.connector.service.MQChooser;
import com.feiyu.connector.service.impl.SimpleClientLoginService;
import com.feiyu.connector.utils.NamedBeanProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 控制消息处理器.
 *
 * @author Zhuff
 */
@Slf4j
public class ControlMsgHandler extends SimpleChannelInboundHandler<Messages.Msg> implements NamedHandler {
  private final MQChooser chooser;
  private final ClientLoginService loginService;
  public ControlMsgHandler() {
    this.chooser = NamedBeanProvider.getSingleton(MQChooser.class);
    this.loginService = new SimpleClientLoginService();
  }

  @Getter
  private Messages.ClientInfo clientInfo = null;
  @Getter
  private long qid;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Messages.Msg msg) throws Exception {
    if (Messages.MsgType.CONTROL.equals(msg.getType())) {
      Messages.ControlMsg controlMsg = msg.getControlMsg();
      if (controlMsg.getType() == Messages.ControlType.CLIENT_LOGIN) {
        this.clientInfo = controlMsg.getClientInfo();
        long qid = chooser.choice(clientInfo);
        log.info("allocate queue {} to client {}", QueueInfoStore.get(qid), clientInfo);
        this.loginService.login(clientInfo, ctx, qid);
        this.qid = qid;
      } else {
        log.info("unknown msg type: {}", msg.getType());
      }
      // 拦截所有控制消息
      return;
    }
    // 当用户已登录 或 消息类型为通知 时放行
    if (loginService.isOnline() || Messages.MsgType.NOTICE.equals(msg.getType())) {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    this.loginService.logout();
    super.channelUnregistered(ctx);
  }

  @Override
  public String name() {
    return "controlMsgHandler";
  }
}
