package com.feiyu.connector.service;

import com.feiyu.base.interfaces.Named;
import com.feiyu.base.proto.Messages;

public interface MQChooser extends Named {
  /**
   * 为客户端分配消息队列 {@link com.feiyu.base.QueueInfo}
   * @param clientInfo 客户端信息
   * @return 队列id
   */
  long choice(Messages.ClientInfo clientInfo);
}
