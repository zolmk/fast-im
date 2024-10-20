package com.feiyu.core.service.impl;

import com.feiyu.core.service.MsgIdentifierService;
import com.feiyu.interfaces.idl.ISequenceService;
import com.feiyu.interfaces.idl.SequenceReq;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SimpleMsgIdentifierService implements MsgIdentifierService {
  //TODO id生成需要换成其他分布式ID生成系统，例如leaf

  @DubboReference
  private ISequenceService sequenceService;
  private AtomicInteger id = new AtomicInteger(1);

  public SimpleMsgIdentifierService() {
  }


  @Override
  public long newId() {
    return id.getAndIncrement();
  }

  @Override
  public long newSeq(long uid) {
    return sequenceService.gen(SequenceReq.newBuilder().setUid(uid).build()).getSeq();
  }
}
