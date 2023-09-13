package com.fy.chatserver.communicate.config;

import com.fy.chatserver.communicate.NamedChannelHandler;
import com.google.protobuf.MessageLite;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.util.NettyRuntime;

import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author zhufeifei 2023/9/10
 **/

public abstract class AcceptorConfig {
    protected int bossCount;
    protected int workerCount;
    protected int readIdleTime;
    protected int writeIdleTime;
    protected int port;
    protected int backlog;


    public abstract void load(Properties properties) throws NumberFormatException;
    public abstract MessageLite getMessageLite();
    public abstract List<Supplier<NamedChannelHandler>> handlers();

    public int getBossCount() {
        return bossCount;
    }

    public void setBossCount(int bossCount) {
        this.bossCount = bossCount;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

}
