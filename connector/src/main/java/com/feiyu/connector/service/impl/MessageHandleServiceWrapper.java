package com.feiyu.connector.service.impl;

import com.feiyu.base.interfaces.Named;
import com.feiyu.base.proto.Messages;
import com.feiyu.interfaces.idl.IMessageHandleService;
import com.feiyu.interfaces.idl.MsgHandleReq;
import com.feiyu.interfaces.idl.MsgHandleRsp;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service(value = "messageHandleService")
public class MessageHandleServiceWrapper implements IMessageHandleService, Named {

  @DubboReference
  private IMessageHandleService iMessageHandleService;

  @Override
  public MsgHandleRsp handle(MsgHandleReq req) {
    return this.iMessageHandleService.handle(req);
  }

  @Override
  public CompletableFuture<MsgHandleRsp> handleAsync(MsgHandleReq req) {
    return this.iMessageHandleService.handleAsync(req);
  }

  @Override
  public String name() {
    return "messageHandleServiceWrapper";
  }
}
