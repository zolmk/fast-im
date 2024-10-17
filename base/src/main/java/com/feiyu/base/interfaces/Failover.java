package com.feiyu.base.interfaces;

/**
 * 故障转移
 * 当任务执行失败时，可以调用failover方法来做故障转移
 */
public interface Failover {
  void failover();
}
