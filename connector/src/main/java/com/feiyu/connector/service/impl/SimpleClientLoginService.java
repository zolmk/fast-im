package com.feiyu.connector.service.impl;

import com.feiyu.base.eventbus.EventBus;
import com.feiyu.base.proto.ProtocolUtil;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.handlers.GenericMsgHandler;
import com.feiyu.connector.service.ClientLoginService;
import com.feiyu.connector.utils.ChannelRegisterEvent;
import com.feiyu.connector.utils.ChannelUnregisterEvent;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;


@Slf4j
public class SimpleClientLoginService implements ClientLoginService {
  private volatile boolean online;
  private long qid;
  private Messages.ClientInfo clientInfo;
  public SimpleClientLoginService() {
    // 初始化参数
    reset();
  }

  @Override
  public boolean isOnline() {
    return this.online;
  }

  @Override
  public CompletableFuture<Boolean> login(Messages.ClientInfo cInfo, ChannelHandlerContext ctx, long qid) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();
    Messages.ClientInfo beforeCInfo = this.clientInfo;
    long beforeQid = this.qid;
    this.clientInfo = cInfo;
    this.qid = qid;
    // 用户登录
    if (beforeCInfo != null) {
      // 说明用户重复登录 或 重连
      CompletableFuture<Void> postFuture = EventBus.post(new ChannelUnregisterEvent(String.valueOf(beforeCInfo.getUid()), beforeQid));
      postFuture.whenCompleteAsync((unused, throwable) -> {
        if (throwable != null) {
          log.error("EventBus.post ChannelUnregisterEvent error. {}", throwable.getMessage());
        }
        future.complete(login0(ctx).join());
      });
      return future;
    }
    // 首次登录
    return login0(ctx);
  }

  @Override
  public void logout() {
    // 检查状态
    if ( ! this.isOnline() || this.qid == -1 || this.clientInfo == null) {
      return;
    }
    EventBus.post(new ChannelUnregisterEvent(String.valueOf(clientInfo.getUid()), this.qid));
    reset();
  }

  private void reset() {
    this.online = false;
    this.clientInfo = null;
    this.qid = -1;
  }

  private CompletableFuture<Boolean> login0(ChannelHandlerContext ctx) {
    if (qid == -1) {
      ctx.writeAndFlush(ProtocolUtil.CONNECT_REST);
      log.info("[login0] qid == -1 error.");
      return CompletableFuture.completedFuture(false);
    }
    // 设置客户端级消息序列号
    ctx.pipeline().get(GenericMsgHandler.class).setCltMsgSeq(this.clientInfo.getCltMsgSeq());

    CompletableFuture<Boolean> future = new CompletableFuture<>();
    // 发布客户端登录注册事件
    CompletableFuture<Void> post = EventBus.post(new ChannelRegisterEvent(String.valueOf(clientInfo.getUid()), ctx.channel(), qid));
    post.whenCompleteAsync((unused, throwable) -> {
      if (throwable == null) {
        // 默认用户有未读消息
        ctx.writeAndFlush(ProtocolUtil.loginSuccess(true));
        // 登录成功
        SimpleClientLoginService.this.online = true;
        future.complete(true);
      } else {
        log.error("client {} login failed. msg: {}", clientInfo.getUid(), throwable.getMessage());
        ChannelFuture channelFuture = ctx.writeAndFlush(ProtocolUtil.CONNECT_REST);
        // 若重置连接消息发送失败，则直接关闭连接
        channelFuture.addListener((ChannelFutureListener) cf -> {
          if ( ! cf.isSuccess()) {
            ctx.channel().close();
          }
        });
        // 登录失败
        future.complete(false);
      }
    });
    return future;
  }
}
