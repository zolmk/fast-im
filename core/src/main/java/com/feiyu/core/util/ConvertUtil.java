package com.feiyu.core.util;

import com.feiyu.base.proto.Messages;
import com.feiyu.core.entity.Msg;

public class ConvertUtil {
  public static Msg toEntityMsg(Messages.Msg msg) {
    Messages.GenericMsg genericMsg = msg.getGenericMsg();
    Messages.Peers peers = genericMsg.getPeers();
    Messages.MsgExtraInfo extraInfo = genericMsg.getExtraInfo();
    Msg msgEntity = new Msg();
    msgEntity.setId(extraInfo.getMsgId());
    msgEntity.setSeq(extraInfo.getSeq());

    msgEntity.setContent(genericMsg.getData().toStringUtf8());
    msgEntity.setType(genericMsg.getType());

    msgEntity.setSender(peers.getFrom());
    msgEntity.setReceiver(peers.getTo());

    return msgEntity;
  }
}
