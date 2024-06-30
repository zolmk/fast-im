package com.feiyu.base.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;

/**
 * 通讯协议工具类.
 *
 * @author Zhuff
 */
public final class ProtoUtil {
  /**
   * 获取心跳包.
   *
   * @param type 心跳包类型 Ping Pong
   * @param data 心跳包可能会携带的数据
   * @return 心跳包
   */
  public static MessageLite heartbeat(Messages.HeartbeatType type, byte[] data) {
    Messages.Msg.Builder builder = Messages.Msg.newBuilder();
    builder.setType(Messages.MsgType.HEARTBEAT);
    builder.setHeartbeatMsg(Messages.HeartbeatMsg.newBuilder().setType(type).setData(ByteString.copyFrom(data)).build());
    return builder.build();
  }
}
