package com.fy.chatserver.communicate;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

import java.io.IOException;

/**
 * @author zhufeifei 2023/9/14
 **/

public class GroupChannel implements IChannel {

    @Override
    public Channel channel() {
        throw new UnsupportedOperationException("Group haven't channel.");
    }

    @Override
    public Future<?> writeAndFlush(Object msg) {
        return null;
    }

    @Override
    public String serviceId() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
