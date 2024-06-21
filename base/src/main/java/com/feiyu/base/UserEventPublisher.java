package com.feiyu.base;

/**
 * 用户事件发布者.
 *
 * @author feiyu
 */
public interface UserEventPublisher<UID, CH> {
  /**
   * 添加用户订阅事件.
   *
   * @param subscriber 订阅者
   * @return 订阅者id，可以用于取消订阅.
   */
  int add(UserEventSubscriber<UID, CH> subscriber);

  /**
   * 移除订阅.
   *
   * @param subscriberId 订阅者的ID
   */
  void remove(int subscriberId);
}
