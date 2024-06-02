package com.feiyu.msgserver.discovery;

import java.util.Properties;

/**
 * @author zhufeifei 2023/9/12
 **/

public class LocalServiceFinderConfig {
    private int peerPort;
    private String peerId;

    public void load(Properties properties) {
        this.peerPort = Integer.parseInt(properties.getProperty("chat-server.local.peer-port", "7777"));
        this.peerId = properties.getProperty("chat-server.local.peer-id", "chat-server-1");
    }

    public int getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(int peerPort) {
        this.peerPort = peerPort;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }
}
