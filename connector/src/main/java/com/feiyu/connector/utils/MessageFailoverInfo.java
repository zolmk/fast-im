package com.feiyu.connector.utils;

import com.feiyu.base.proto.Messages;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageFailoverInfo {
  private String to;
  private Messages.Msg msg;
  private Reason reason;

  /**
   * 消息发送失败原因
   */
  public enum Reason {
    /**
     * 客户端已关闭
     */
    CLIENT_CLOSED,
    /**
     * 队列未绑定
     */
    QUEUE_UNMOUNT,
    /**
     * 发送次数达到最大值（耗尽）
     */
    SEND_EXHAUSTED
  }
}
