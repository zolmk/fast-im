package com.feiyu.msgserver.communicate.event;

import io.netty.channel.Channel;

/**
 * @author zhufeifei 2023/11/12
 **/

public class UserRegisterEvent {
    public UserRegisterEvent(String uid, Channel channel) {
        this.channel = channel;
        this.uid = uid;
    }

    private final String uid;
    private final Channel channel;

    public String getUid() {
        return uid;
    }

    public Channel getChannel() {
        return channel;
    }
}
