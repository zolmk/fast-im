package com.feiyu.base.interfaces;

/**
 * 可撤销
 * 如果一个任务是可以撤销的，那么可以实现该接口
 */
public interface Revocable {
  /**
   * 将任务标记为撤销状态
   */
  void revoke();

  /**
   * 检查任务是否已被撤销
   * @return
   */
  boolean isRevoked();
}
