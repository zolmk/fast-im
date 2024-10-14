package com.feiyu.connector.service;

import com.feiyu.base.proto.Messages;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CompletableFuture;

public interface ClientLoginService {
  /**
   * 检查客户端是否在线
   * @return
   */
  boolean isOnline();

  /**
   * 客户端登录
   * @param clientInfo 客户端信息
   * @param ctx
   * @param qid
   * @return
   */
  CompletableFuture<Boolean> login(Messages.ClientInfo clientInfo, ChannelHandlerContext ctx, long qid);

  /**
   * 客户端登出
   */
  void logout();
}
