package com.feiyu.route.util;

import com.feiyu.base.proto.Messages;
import com.feiyu.base.utils.CloseFuture;

import java.io.Closeable;

public interface Producer extends Closeable {
  void produce(long to, Messages.Msg msg);
  boolean isAlive();
}
