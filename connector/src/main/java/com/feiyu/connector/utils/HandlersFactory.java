package com.feiyu.connector.utils;

import io.netty.channel.ChannelHandler;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * 处理器工厂
 * @author zhufeifei 2024/10/6
 **/

public class HandlersFactory {
    public static Supplier<ChannelHandler> create(String clazz, Properties properties) throws Exception {
        Class<?> c = Class.forName(clazz);
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
        // is shared
        if (c.isAnnotationPresent(ChannelHandler.Sharable.class)) {
            Object o = null;
            if (hasParams) {
                o = constructor.newInstance(properties);
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
                        return (ChannelHandler) finalConstructor.newInstance(properties);
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
        return supplier;
    }
}
