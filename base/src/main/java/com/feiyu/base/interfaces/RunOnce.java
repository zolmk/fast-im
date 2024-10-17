package com.feiyu.base.interfaces;

import java.io.Closeable;

public interface RunOnce extends Revocable, Closeable {
  void runOnce() throws Exception;
}
