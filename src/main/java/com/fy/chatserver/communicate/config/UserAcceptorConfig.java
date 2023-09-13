package com.fy.chatserver.communicate.config;

import com.fy.chatserver.communicate.NamedChannelHandler;
import com.fy.chatserver.communicate.ProtobufProvider;
import com.fy.chatserver.communicate.proto.ClientProto;
import com.google.protobuf.MessageLite;
import io.netty.util.NettyRuntime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author zhufeifei 2023/9/10
 **/

public class UserAcceptorConfig extends AcceptorConfig{
    private final List<Supplier<NamedChannelHandler>> suppliers;

    @SafeVarargs
    public UserAcceptorConfig(Supplier<NamedChannelHandler>... suppliers) {
        this.suppliers = new ArrayList<>();
        Collections.addAll(this.suppliers, suppliers);
    }
    @Override
    public void load(Properties properties) throws NumberFormatException {
        this.backlog = Integer.parseInt(properties.getProperty("chat-server.user.accepter.backlog", "128"));
        this.bossCount = Integer.parseInt(properties.getProperty("chat-server.user.accepter.boss-count", String.valueOf(NettyRuntime.availableProcessors())));
        this.workerCount = Integer.parseInt(properties.getProperty("chat-server.user.accepter.worker-count", String.valueOf(NettyRuntime.availableProcessors() * 2)));
        this.readIdleTime = Integer.parseInt(properties.getProperty("chat-server.user.accepter.read-idle-time", "20"));
        this.writeIdleTime = Integer.parseInt(properties.getProperty("chat-server.user.accepter.write-idle-time", "120"));
        this.port = Integer.parseInt(properties.getProperty("chat-server.user.accepter.port", "7777"));
    }

    @Override
    public MessageLite getMessageLite() {
        return ProtobufProvider.forUser();
    }

    @Override
    public List<Supplier<NamedChannelHandler>> handlers() {
        return this.suppliers;
    }
}
