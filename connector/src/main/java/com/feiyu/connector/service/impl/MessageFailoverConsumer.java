package com.feiyu.connector.service.impl;

import com.feiyu.base.proto.Messages;
import com.feiyu.base.proto.ProtocolUtil;
import com.feiyu.connector.utils.MessageFailoverInfo;
import com.feiyu.interfaces.idl.IMessageHandleService;
import com.feiyu.interfaces.idl.MsgHandleReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;


@Slf4j
@Component
public class MessageFailoverConsumer implements Consumer<MessageFailoverInfo> {
  private final IMessageHandleService handleService;

  public MessageFailoverConsumer(IMessageHandleService messageHandleService) {
    this.handleService = messageHandleService;
  }

  @Override
  public void accept(MessageFailoverInfo messageFailoverInfo) {
    Messages.Msg msg = messageFailoverInfo.getMsg();
    if (Messages.MsgType.GENERIC != msg.getType()) {
      // 非聊天消息，忽略
      return;
    }
    log.info("failover msg: {}", messageFailoverInfo);
    Messages.GenericMsg genericMsg = msg.getGenericMsg();
    switch (messageFailoverInfo.getReason()) {
      case CLIENT_CLOSED: {
        log.info("client {} closed", messageFailoverInfo.getTo());
      } break;
      case QUEUE_UNMOUNT: {
        log.info("queue unmount: {}", messageFailoverInfo.getTo());
      } break;
      default: {
        log.info("unknown reason: {}", messageFailoverInfo.getReason());
      }
    }
    Messages.Msg reqMsg = ProtocolUtil.messageDeliverFailed(genericMsg.getExtraInfo().getMsgId());
    MsgHandleReq req = MsgHandleReq.newBuilder().setTo(genericMsg.getPeers().getTo()).setMsg(reqMsg).build();
    handleService.handle(req);
  }
}
