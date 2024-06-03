package com.feiyu.core.communicate;

import com.feiyu.core.communicate.proto.ServerProto;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

/**
 * @author zhufeifei 2023/9/8
 **/

public class RemoteChannel extends AbstractChannel {

    public RemoteChannel(String serviceId, Channel channel) {
        super(serviceId, channel);
    }

    @Override
    public Future<?> writeAndFlush(Object msg) {
        return super.writeAndFlush(msg, ServerProto.SInner.class);
    }
}
