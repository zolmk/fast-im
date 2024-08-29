package com.feiyu.interfaces;

import com.google.protobuf.MessageLite;

/**
 * 消息处理服务.
 *
 * @author feiyu
 */

public interface IMessageHandleService {
    void handle(MessageLite message);
}
