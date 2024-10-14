package com.feiyu.base;

public interface RunOnce extends Revocable {
  void runOnce() throws Exception;
}
