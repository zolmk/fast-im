package com.feiyu.route.service;

import com.feiyu.base.interfaces.Named;
import com.feiyu.base.proto.Messages;

import java.io.Closeable;


public interface MessageProducer extends Named, Closeable {
  void produce(long to, Messages.Msg msg) throws Exception;
}
