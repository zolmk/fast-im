package com.fy.chatserver.discovery;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author zhufeifei 2023/9/12
 **/

public class LocalServiceFinder implements ServiceFinder {
    private final LocalServiceFinderConfig config;

    public LocalServiceFinder(LocalServiceFinderConfig config) {
        this.config = config;
    }
    @Override
    public String findService(String clientId) {
        return this.config.getPeerId();
    }

    @Override
    public InetSocketAddress getServiceAddress(String serviceId) {
        return new InetSocketAddress("127.0.0.1", this.config.getPeerPort());
    }

    @Override
    public boolean keepalive(String serviceId) {
        return true;
    }

    @Override
    public void registerClient(String serviceId, String clientId) {

    }

    @Override
    public void registerServer(String serviceId, String connectString) {

    }

    @Override
    public void close() throws IOException {

    }
}
