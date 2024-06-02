package com.feiyu.msgserver.communicate;

import io.netty.channel.Channel;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;

import java.io.Closeable;

/**
 * @author zhufeifei 2023/9/8
 **/

public interface IChannel extends Closeable {
    Channel channel();
    Future<?> writeAndFlush(Object msg);
    String serviceId();

    default boolean isWritable() {
        return channel() != null && channel().isWritable();
    }
}
