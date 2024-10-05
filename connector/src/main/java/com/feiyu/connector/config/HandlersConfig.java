package com.feiyu.connector.config;

import com.feiyu.connector.handlers.BaseHandler;
import com.feiyu.connector.utils.HandlersFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

/**
 * handlers的配置类
 *
 * @author Zhuff
 */
public class HandlersConfig {
  private final static List<Supplier<ChannelHandler>> CLIENT_HANDLERS;
  private final static Properties HANDLERS_PROPERTIES;

  static {
    CLIENT_HANDLERS = new ArrayList<>();
    HANDLERS_PROPERTIES = new Properties();
    try (InputStream is =
           Objects.requireNonNull(HandlersConfig.class.getClassLoader().getResourceAsStream(
             "handlers.properties"))) {
      HANDLERS_PROPERTIES.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try (Scanner scanner = new Scanner(Objects.requireNonNull(HandlersConfig.class.getClassLoader().getResourceAsStream(
      "handlers.factories")))) {
      while (scanner.hasNext()) {
        String clName = scanner.nextLine();
        Supplier<ChannelHandler> supplier = HandlersFactory.create(clName, HANDLERS_PROPERTIES);
        CLIENT_HANDLERS.add(supplier);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void initPipeline(ChannelPipeline pipeline) {
    for (Supplier<ChannelHandler> handlerSupplier : CLIENT_HANDLERS) {
      ChannelHandler channelHandler = handlerSupplier.get();
      if (channelHandler instanceof BaseHandler) {
        pipeline.addLast(((BaseHandler) channelHandler).name(), channelHandler);
      } else {
        pipeline.addLast(channelHandler);
      }
    }
  }
}
