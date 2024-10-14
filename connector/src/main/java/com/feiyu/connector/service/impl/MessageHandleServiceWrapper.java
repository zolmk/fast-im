package com.feiyu.connector.service.impl;

import com.feiyu.base.Named;
import com.feiyu.base.proto.Messages;
import com.feiyu.interfaces.idl.IMessageHandleService;
import com.feiyu.interfaces.idl.MsgHandleRsp;
import com.google.protobuf.Empty;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service(value = "messageHandleService")
public class MessageHandleServiceWrapper implements IMessageHandleService, Named {

  @DubboReference
  private IMessageHandleService iMessageHandleService;

  @Override
  public MsgHandleRsp handle(Messages.Msg message) {
    return this.iMessageHandleService.handle(message);
  }

  @Override
  public CompletableFuture<MsgHandleRsp> handleAsync(Messages.Msg request) {
    return this.iMessageHandleService.handleAsync(request);
  }

  @Override
  public String name() {
    return "messageHandleServiceWrapper";
  }
}
