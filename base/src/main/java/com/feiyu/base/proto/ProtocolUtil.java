package com.feiyu.base.proto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ProtocolUtil {
  public final static Messages.Msg PING = heartbeat(Messages.HeartbeatType.PING);
  public final static Messages.Msg PONG = heartbeat(Messages.HeartbeatType.PONG);

  public final static Messages.Msg CONNECT_REST = Messages.Msg.newBuilder().setType(Messages.MsgType.CONTROL).setControlMsg(Messages.ControlMsg.newBuilder().setType(Messages.ControlType.CONNECT_REST).build()).build();

  /**
   * 登录成功消息
   * @param hasUnread
   * @return
   */
  public static Messages.Msg loginSuccess(boolean hasUnread) {
    return Messages.Msg.newBuilder().setType(Messages.MsgType.NOTICE).setNoticeMsg(Messages.NoticeMsg.newBuilder().setType(Messages.NoticeType.LOGIN_ACK).setHasUnread(hasUnread).build()).build();
  }

  /**
   * 构造消息ack
   * @param msgId
   * @param cltSeq
   * @return
   */
  public static Messages.Msg messageRevAck(long msgId, long cltSeq) {

    Messages.MsgRevAck msgRevAck = Messages.MsgRevAck.newBuilder()
      .setMsgId(msgId)
      .setCltSeq(cltSeq)
      .build();
    Messages.NoticeMsg noticeMsg = Messages.NoticeMsg.newBuilder()
      .setMsgRevAck(msgRevAck)
      .setType(Messages.NoticeType.RECEIVED_ACK)
      .build();
    return Messages.Msg.newBuilder()
      .setType(Messages.MsgType.NOTICE)
      .setNoticeMsg(noticeMsg)
      .build();
  }


  /**
   * 判断是否是 Challenge Ack
   * @param noticeMsg
   * @return
   */
  public static boolean isChallengeAck(Messages.NoticeMsg noticeMsg) {
    return Objects.requireNonNull(noticeMsg.getMsgRevAck()).getMsgId() == -1L;
  }

  /**
   * 消息已投递通知
   * 由服务端发送给消息发送方
   * @param msgId
   * @return
   */
  public static Messages.Msg messageDeliveredNotice(long msgId) {
    Messages.NoticeMsg noticeMsg = Messages.NoticeMsg.newBuilder()
      .setType(Messages.NoticeType.DELIVERED)
      .setMsgId(msgId)
      .build();
    return Messages.Msg.newBuilder()
      .setType(Messages.MsgType.NOTICE)
      .setNoticeMsg(noticeMsg)
      .build();
  }

  /**
   * 消息已读通知
   * @param msgId
   * @return
   */
  public static Messages.Msg messageReadNotice(long msgId) {
    Messages.NoticeMsg noticeMsg = Messages.NoticeMsg.newBuilder()
      .setType(Messages.NoticeType.READ)
      .setMsgId(msgId)
      .build();
    return Messages.Msg.newBuilder()
      .setType(Messages.MsgType.NOTICE)
      .setNoticeMsg(noticeMsg)
      .build();
  }

  /**
   * 构造消息已投递ack
   * @param msgId
   * @param from
   * @return
   */
  public static Messages.Msg messageDeliveredAck(long msgId, long from) {
    return messageDeliveredAck(Collections.singletonList(msgId), Collections.singletonList(from));
  }

  public static Messages.Msg messageDeliveredAck(List<Long> msgIds, List<Long> froms) {
    Messages.MsgDeliveryAck deliveryAck = genMsgDeliveryAck(msgIds, froms);
    return Messages.Msg.newBuilder()
      .setType(Messages.MsgType.NOTICE)
      .setNoticeMsg(Messages.NoticeMsg.newBuilder()
        .setType(Messages.NoticeType.DELIVERED_ACK)
        .setMsgDeliverAck(deliveryAck).build())
      .build();
  }

  private static Messages.MsgDeliveryAck genMsgDeliveryAck(List<Long> msgIds, List<Long> froms) {
    if (msgIds.size() != froms.size()) {
      throw new IllegalArgumentException("msgIds.size() != froms.size()");
    }
    Messages.MsgDeliveryAck.Builder builder = Messages.MsgDeliveryAck.newBuilder();
    for (int i = 0; i < msgIds.size(); i++) {
      builder.addMsgId(msgIds.get(i));
      builder.addFrom(froms.get(i));
    }
    return builder.build();
  }

  public static Messages.Msg messageReadAck(long msgId, long from) {
    return messageReadAck(Collections.singletonList(msgId), Collections.singletonList(from));
  }

  /**
   * 构造已读ack
   * @return
   */
  public static Messages.Msg messageReadAck(List<Long> msgIds, List<Long> froms) {
    Messages.MsgDeliveryAck deliveryAck = genMsgDeliveryAck(msgIds, froms);
    Messages.NoticeMsg noticeMsg = Messages.NoticeMsg.newBuilder().setType(Messages.NoticeType.READ_ACK).setMsgDeliverAck(deliveryAck).build();
    return Messages.Msg.newBuilder().setType(Messages.MsgType.NOTICE).setNoticeMsg(noticeMsg).build();
  }


  /**
   * 消息投递失败
   * @param msgId
   * @return
   */
  public static Messages.Msg messageDeliverFailed(long msgId) {
    Messages.ControlMsg controlMsg = Messages.ControlMsg.newBuilder().setType(Messages.ControlType.MSG_DELIVERED_FAIL).setMsgId(msgId).build();
    return Messages.Msg.newBuilder().setType(Messages.MsgType.CONTROL).setControlMsg(controlMsg).build();
  }



  private static Messages.Msg heartbeat(Messages.HeartbeatType type) {
    return Messages.Msg.newBuilder().setType(Messages.MsgType.HEARTBEAT).setHeartbeatMsg(heartbeatMsg(type)).build();
  }

  private static Messages.HeartbeatMsg heartbeatMsg(Messages.HeartbeatType type) {
    return Messages.HeartbeatMsg.newBuilder().setType(type).build();
  }

}
