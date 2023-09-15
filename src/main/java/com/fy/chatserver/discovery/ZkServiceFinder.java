package com.fy.chatserver.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author zhufeifei 2023/9/9
 **/

public class ZkServiceFinder implements ServiceFinder{
    private static final Logger LOG = LoggerFactory.getLogger(ZkServiceFinder.class);
    private final CuratorCache clientCache;
    private final CuratorCache serviceCache;
    private final CuratorFramework client;
    private final String monitorClientPath;
    private final String monitorServicePath;

    public ZkServiceFinder(String rootPath, String connectString) {
        this.monitorClientPath =  rootPath + "/client";
        this.monitorServicePath = rootPath + "/server";
        this.client = CuratorFrameworkFactory.newClient(connectString, 5000, 10000, new RetryNTimes(10, 2000));
        this.clientCache = CuratorCache.build(this.client, this.monitorClientPath);
        this.serviceCache = CuratorCache.build(this.client, this.monitorServicePath);
        this.client.start();
        this.clientCache.start();
        this.serviceCache.start();
    }
    @Override
    public String findService(String clientId) {
        Optional<ChildData> childData = this.clientCache.get(toClientPath(clientId));
        if (!childData.isPresent()) {
            return null;
        }
        ChildData data = childData.get();
        return new String(data.getData(), StandardCharsets.UTF_8);
    }

    @Override
    public InetSocketAddress getServiceAddress(String serviceId) {
        Optional<ChildData> childDataOptional = this.serviceCache.get(toServerPath(serviceId));
        if (!childDataOptional.isPresent()) {
            return null;
        }
        ChildData data = childDataOptional.get();
        String[] sts = new String(data.getData(), StandardCharsets.UTF_8).split(":");
        if (sts.length != 2) {
            LOG.error("the data of {} is error.", data.getPath());
            return null;
        }
        try {
            return new InetSocketAddress(sts[0], Integer.parseInt(sts[1]));
        } catch (NumberFormatException e) {
            LOG.error("the data of format is error.", e);
            return null;
        }
    }

    private String toClientPath(String clientId) {
        return this.monitorClientPath + "/" + clientId;
    }

    private String toServerPath(String serviceId) {
        return this.monitorServicePath + "/" + serviceId;
    }



    @Override
    public boolean keepalive(String serviceId) {
        return serviceCache.get(toServerPath(serviceId)).isPresent();
    }

    @Override
    public void registerClient(String serviceId, String clientId) {
        try {
            this.client.create().withMode(CreateMode.EPHEMERAL).forPath(toClientPath(clientId), serviceId.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOG.error("occur error when register client.", e);
        }
    }

    @Override
    public void unregisterClient(String serviceId, String clientId) {
        try {
            this.client.delete().forPath(toClientPath(clientId));
        } catch (Exception e) {
            LOG.error("occur error when unregister client.", e);
        }
    }

    @Override
    public void registerServer(String serviceId, String connectString) {
        try {
            this.client.create().withMode(CreateMode.EPHEMERAL).forPath(toServerPath(serviceId), connectString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterServer(String serviceId) {

    }

    @Override
    public void close() throws IOException {
        this.clientCache.close();
        this.serviceCache.close();
        this.client.close();
    }
}
