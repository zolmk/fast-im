package com.feiyu.connector.handlers;

import com.feiyu.base.proto.Messages;
import com.feiyu.base.proto.ProtoUtil;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一的心跳处理器.
 *
 * @author Zhuff
 */
@Slf4j
public class UnifiedHeartbeatHandler extends SimpleChannelInboundHandler<Messages.Msg>
    implements BaseHandler {
  private HeartbeatCallback heartbeatCallback;
  private ScheduledFuture<?> future;
  private final int timeout = 3;
  private final TimeUnit timeUnit = TimeUnit.SECONDS;
  private final int retryCount = 3;

  private final Function<Channel, HeartbeatCallback> heartbeatCallbackSupplier =
      (channel) -> new HeartbeatCallback(retryCount, timeout, timeUnit, channel, () -> {
        if (log.isInfoEnabled()) {
          log.info("heartbeat timeout. the channel will close.");
        }
        channel.close();
      });

  public UnifiedHeartbeatHandler() {
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    heartbeatCallback = heartbeatCallbackSupplier.apply(ctx.channel());
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Messages.Msg msg) throws Exception {
    if (Messages.MsgType.HEARTBEAT.equals(msg.getType())) {
      Messages.HeartbeatMsg heartbeatMsg = msg.getHeartbeatMsg();
      if (Messages.HeartbeatType.PONG.equals(heartbeatMsg.getType())) {
        if (!this.future.cancel(false)) {
          // 如果是 PONG 包，并且取消任务失败，则重新构建一个HeartbeatCallback
          this.heartbeatCallback.destroy();
          this.heartbeatCallback = heartbeatCallbackSupplier.apply(ctx.channel());
        }
        this.heartbeatCallback.reset();
      } else {
        // 如果是 PING 包，则回复 PONG
        ctx.writeAndFlush(ProtoUtil.heartbeat(Messages.HeartbeatType.PONG, new byte[0]));
      }
    }
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      if (IdleState.WRITER_IDLE.equals(((IdleStateEvent) evt).state())) {
        // 写空闲超时，写 PING 包
        ctx.writeAndFlush(ProtoUtil.heartbeat(Messages.HeartbeatType.PING, new byte[0]));
        this.future = ctx.channel().eventLoop().schedule(this.heartbeatCallback, timeout, timeUnit);
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

  private static class HeartbeatCallback implements Runnable {
    private final int originRetry;
    private int retry;
    private final Channel channel;

    private final Runnable fail;

    private final int timeout;

    private final TimeUnit timeUnit;

    private boolean isDestroy;

    public HeartbeatCallback(int retry, int timeout, TimeUnit timeUnit, Channel channel, Runnable fail) {
      this.originRetry = this.retry = retry;
      this.timeout = timeout;
      this.timeUnit = timeUnit;
      this.channel = channel;
      this.fail = fail;
      this.isDestroy = false;
    }

    public void reset() {
      this.retry = this.originRetry;
    }

    public void destroy() {
      this.isDestroy = true;
    }

    @Override
    public void run() {
      if (this.isDestroy) {
        return;
      }
      if (channel.isWritable() && this.retry > 0) {
        channel.writeAndFlush(ProtoUtil.heartbeat(Messages.HeartbeatType.PING, new byte[0]));
        channel.eventLoop().schedule(this, this.timeout, this.timeUnit);
        this.retry--;
      } else {
        fail.run();
      }
    }
  }
}
