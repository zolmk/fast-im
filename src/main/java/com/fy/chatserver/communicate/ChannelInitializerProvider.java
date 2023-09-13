package com.fy.chatserver.communicate;

import com.fy.chatserver.communicate.config.AcceptorConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * @author zhufeifei 2023/9/13
 **/

public class ChannelInitializerProvider {

    public static ChannelInitializer<? extends Channel> forUser(AcceptorConfig config) {
        return new CsChannelInitializer(config);
    }

    public static ChannelInitializer<? extends Channel> forRemoteServer(AcceptorConfig config) {
        return new CsChannelInitializer(config);
    }
}
