package com.feiyu.connector.service.impl;

import com.feiyu.base.Revocable;
import com.feiyu.base.proto.Messages;
import com.feiyu.base.proto.ProtocolUtil;
import com.feiyu.connector.service.NoticeHandleService;
import com.feiyu.connector.utils.CommonExecutor;
import com.feiyu.connector.utils.NamedBeanProvider;
import com.feiyu.interfaces.idl.IMessageHandleService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.feiyu.base.proto.ProtocolUtil.*;

@Slf4j
public class SimpleNoticeHandleService implements NoticeHandleService {
  private Map<Long, Revocable> tasks;
  private final IMessageHandleService messageHandleService;
  public SimpleNoticeHandleService() {
    this.tasks = new HashMap<Long, Revocable>();
    this.messageHandleService = NamedBeanProvider.getSingleton(MessageHandleServiceWrapper.class);
  }

  @Override
  public void handleDeliveryAck(Messages.NoticeMsg noticeMsg) {
    // 消息已送达 ack。客户端接收到消息后，需要向connector发送ack回复
    Messages.MsgDeliveryAck deliveryAck = noticeMsg.getMsgDeliverAck();
    for (Long msgId : deliveryAck.getMsgIdList()) {
      if (tasks.containsKey(msgId)) {
        tasks.get(msgId).revoke();
        this.unregisterRevocableTask(msgId);
      } else {
        log.info("unknown received ack message {}", noticeMsg);
      }
      // 避免在IO线程中执行耗时操作，放入到公共线程池中执行
      CommonExecutor.getExecutor().execute(() -> {
        try {
          // 发送消息已投递通知
          this.messageHandleService.handle(ProtocolUtil.messageDeliveredNotice(msgId));
        } catch (Exception e) {
          log.error("message {} delivered notice send error", msgId, e);
        }
      });
    }
  }

  @Override
  public void handleReadAck(Messages.NoticeMsg noticeMsg) {
    CommonExecutor.getExecutor().execute(() -> {
      try {
        this.messageHandleService.handle(messageReadNotice(noticeMsg.getMsgId()));
      } catch (Exception e) {
        log.error("message {} read notice send error", noticeMsg.getMsgId(), e);
      }
    });
  }

  @Override
  public void registerRevocableTask(Long msgId, Revocable revocable) {
    this.tasks.put(msgId, revocable);
  }

  @Override
  public void unregisterRevocableTask(Long msgId) {
    this.tasks.remove(msgId);
  }

  @Override
  public String name() {
    return "simpleNoticeHandleService";
  }
}
