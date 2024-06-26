package com.feiyu.core.communicate;

import com.feiyu.core.communicate.proto.ClientProto;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

/**
 * @author zhufeifei 2023/9/8
 **/

public class UserChannel extends AbstractChannel{

    public UserChannel(String serviceId, Channel channel) {
        super(serviceId, channel);
    }

    public Future<?> writeAndFlush(Object msg) {
        return this.writeAndFlush(msg, ClientProto.CInner.class);
    }
}
