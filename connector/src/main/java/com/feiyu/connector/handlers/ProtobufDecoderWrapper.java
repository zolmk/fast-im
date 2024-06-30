package com.feiyu.connector.handlers;

import com.google.protobuf.MessageLite;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * #{@link ProtobufDecoder}的包装类
 *
 * @author Zhuff
 */
public class ProtobufDecoderWrapper extends ProtobufDecoder implements BaseHandler{
  public ProtobufDecoderWrapper(Properties properties) {
    super(messageLite(properties.getProperty("client.message-lite")));
  }

  /**
   * 获取#{@link MessageLite}对象来完成ProtobufDecoder的初始化.
   *
   * @param cname MessageLite对象的全类名
   * @return MessageLite
   */
  public static MessageLite messageLite(String cname) {
    MessageLite messageLite;
    try {
      Class<?> clazz = Class.forName(cname);
      Method method = clazz.getDeclaredMethod("getDefaultInstance");
      messageLite = (MessageLite) method.invoke(null);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return messageLite;
  }

  @Override
  public String name() {
    return ProtobufDecoderWrapper.class.getSimpleName();
  }
}
