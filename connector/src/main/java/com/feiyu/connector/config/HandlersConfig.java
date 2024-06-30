package com.feiyu.connector.config;

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
        Class<?> c = Class.forName(clName);
        Supplier<ChannelHandler> supplier = null;
        Constructor<?> constructor;
        boolean hasParams;
        try {
          constructor = c.getConstructor(Properties.class);
          hasParams = true;
        } catch (NoSuchMethodException e) {
          constructor = c.getConstructor();
          hasParams = false;
        }
        if (c.isAnnotationPresent(ChannelHandler.Sharable.class)) {
          Object o = null;
          if (hasParams) {
            o = constructor.newInstance(HANDLERS_PROPERTIES);
          } else {
            o = constructor.newInstance();
          }
          Object finalO = o;
          supplier = () -> (ChannelHandler) finalO;
        } else {
          Constructor<?> finalConstructor = constructor;
          if (hasParams) {
            supplier = () -> {
              try {
                return (ChannelHandler) finalConstructor.newInstance(HANDLERS_PROPERTIES);
              } catch (InstantiationException | IllegalAccessException |
                       InvocationTargetException e) {
                throw new RuntimeException(e);
              }
            };
          } else {
            supplier = () -> {
              try {
                return (ChannelHandler) finalConstructor.newInstance();
              } catch (InstantiationException | IllegalAccessException |
                       InvocationTargetException e) {
                throw new RuntimeException(e);
              }
            };
          }
        }
        CLIENT_HANDLERS.add(supplier);
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
             NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static void initPipeline(ChannelPipeline pipeline) {
    for (Supplier<ChannelHandler> handlerSupplier : CLIENT_HANDLERS) {
      pipeline.addLast(handlerSupplier.get());
    }
  }
}
