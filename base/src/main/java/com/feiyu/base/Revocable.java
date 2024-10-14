package com.feiyu.base;

/**
 * 可撤销
 * 如果一个任务是可以撤销的，那么可以实现该接口
 */
public interface Revocable {
  void revoke();
  boolean isRevoked();
}
