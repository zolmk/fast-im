package com.feiyu.connector.service;

import com.feiyu.base.interfaces.Named;
import com.feiyu.base.interfaces.Revocable;
import com.feiyu.base.proto.Messages;

/**
 * 处理通知消息的Service
 */
public interface NoticeHandleService extends Named {

  /**
   * 处理已送达确认
   */
  void handleDeliveryAck(Messages.Msg msg);

  /**
   * 处理已读确认
   */
  void handleReadAck(Messages.Msg msg);

  /**
   * 注册可撤销的任务
   * @param msgId
   * @param revocable
   */
  void registerRevocableTask(Long msgId, Revocable revocable);


  /**
   * 取消注册的任务
   * @param msgId
   */
  void unregisterRevocableTask(Long msgId);
}
