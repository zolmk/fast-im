package com.feiyu.connector.handlers;

import com.feiyu.base.proto.ProtoUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一的心跳处理器.
 *
 * @author Zhuff
 */
@Slf4j
public class UnifiedHeartbeatHandler extends ChannelInboundHandlerAdapter implements BaseHandler {
  public UnifiedHeartbeatHandler() {
  }
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      if (IdleState.WRITER_IDLE.equals(((IdleStateEvent) evt).state())) {
        ctx.writeAndFlush(ProtoUtil.heartbeat(new byte[0]));
      } else if (IdleState.READER_IDLE.equals(((IdleStateEvent) evt).state())) {
        log.info("The channel unread too many time.");
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
  @Override
  public String name() {
    return UnifiedHeartbeatHandler.class.getSimpleName();
  }
}
