package com.feiyu.msgserver.communicate.config;

import com.feiyu.msgserver.communicate.NamedChannelHandler;
import com.feiyu.msgserver.communicate.ProtobufProvider;
import com.google.protobuf.MessageLite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * configuration for current hostname actively start connect the remote hostname
 * @author zolmk
 */
public class RemoteChannelConfig {
    protected int workerCount;
    protected int readIdleTime;
    protected int writeIdleTime;
    protected final List<Supplier<NamedChannelHandler>> handlers;

    @SafeVarargs
    public RemoteChannelConfig(Supplier<NamedChannelHandler>... channelHandlers) {
        this.workerCount = 2;
        this.readIdleTime = 0;
        this.writeIdleTime = 0;
        this.handlers = new ArrayList<>();
        Collections.addAll(this.handlers, channelHandlers);
    }

    public void load(Properties properties) {
        this.workerCount = Integer.parseInt(properties.getProperty("chat-server.remote.worker-count", "2"));
        this.readIdleTime = Integer.parseInt(properties.getProperty("chat-server.remote.read-idle-time", "10"));
        this.writeIdleTime = Integer.parseInt(properties.getProperty("chat-server.remote.write-idle-time", "10"));
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public int getReadIdleTime() {
        return readIdleTime;
    }

    public void setReadIdleTime(int readIdleTime) {
        this.readIdleTime = readIdleTime;
    }

    public int getWriteIdleTime() {
        return writeIdleTime;
    }

    public void setWriteIdleTime(int writeIdleTime) {
        this.writeIdleTime = writeIdleTime;
    }

    public MessageLite getMessageLite() {
        return ProtobufProvider.forServer();
    }

    public List<Supplier<NamedChannelHandler>> getHandlers() {
        return handlers;
    }
}
