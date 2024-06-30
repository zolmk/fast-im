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
   * @param bytes 心跳包可能会携带的数据
   * @return 心跳包
   */
  public static MessageLite heartbeat(byte[] bytes) {
    Messages.Msg.Builder builder = Messages.Msg.newBuilder();
    builder.setType(Messages.MsgType.HEARTBEAT);
    builder.setEmpty(bytes.length == 0 ? ByteString.EMPTY : ByteString.copyFrom(bytes));
    return builder.build();
  }
}
