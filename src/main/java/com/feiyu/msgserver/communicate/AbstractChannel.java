package com.feiyu.msgserver.communicate;

import io.netty.channel.Channel;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.IOException;

/**
 * @author zhufeifei 2023/9/8
 **/

public abstract class AbstractChannel implements IChannel {
    private final String serviceId;
    private final Channel nettyChannel;
    public AbstractChannel(String serviceId, Channel channel) {
        this.serviceId = serviceId;
        this.nettyChannel = channel;
    }
    @Override
    public Channel channel() {
        return this.nettyChannel;
    }

    @Override
    public String serviceId() {
        return this.serviceId;
    }

    @Override
    public void close() throws IOException {
        if (channel() != null && channel().isOpen()) {
            channel().close();
        }
    }

    protected Future<?> writeAndFlush(Object msg, Class<?> suitableType) {
        if (!isWritable()) {
            return new FailedFuture<>(GlobalEventExecutor.INSTANCE,
                    new RuntimeException(String.format("%s channel cannot writeable.", serviceId())));
        }
        if (!(suitableType.isInstance(msg))) {
            return new FailedFuture<>(
                    GlobalEventExecutor.INSTANCE,
                    new RuntimeException(String.format("%s only support %s msg", this.getClass().getName(), suitableType.getName())));
        }
        return channel().writeAndFlush(msg);
    }
}
