package com.feiyu.connector.service;

import com.feiyu.base.Named;
import com.feiyu.base.Revocable;
import com.feiyu.base.proto.Messages;

/**
 * 处理通知消息的Service
 */
public interface NoticeHandleService extends Named {

  /**
   * 处理已送达确认
   * @param noticeMsg
   */
  void handleDeliveryAck(Messages.NoticeMsg noticeMsg);

  /**
   * 处理已读确认
   * @param noticeMsg
   */
  void handleReadAck(Messages.NoticeMsg noticeMsg);

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
