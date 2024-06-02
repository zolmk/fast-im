package com.feiyu.msgserver.communicate;

import io.netty.channel.ChannelHandler;

/**
 * @author zhufeifei 2023/9/13
 **/


public interface NamedChannelHandler extends ChannelHandler {
    String name();
}
