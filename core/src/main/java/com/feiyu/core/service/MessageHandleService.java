package com.feiyu.core.service;

import com.feiyu.base.proto.Messages;
import com.feiyu.interfaces.idl.IMessageHandleService;
import com.feiyu.interfaces.idl.MsgHandleRsp;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@DubboService
public class MessageHandleService implements IMessageHandleService {
  @Override
  public MsgHandleRsp handle(Messages.Msg message) {
    log.info("handle message {}", message);
    return MsgHandleRsp.getDefaultInstance();
  }

  @Override
  public CompletableFuture<MsgHandleRsp> handleAsync(Messages.Msg request) {
    log.info("handleAsync message {}", request);
    return CompletableFuture.completedFuture(MsgHandleRsp.getDefaultInstance());
  }
}
