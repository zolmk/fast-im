package com.fy.chatserver.discovery;

import cn.hutool.core.lang.UUID;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author zhufeifei 2023/9/15
 **/

public class ZkGroupFinder implements GroupFinder {
    private static final Logger LOG = LoggerFactory.getLogger(ZkGroupFinder.class);
    private final String rootPath;
    private final String connectString;
    private final CuratorFramework client;

    public ZkGroupFinder(String rootPath, String connectString) {
        this.rootPath = rootPath + "/group";
        this.connectString = connectString;
        this.client = CuratorFrameworkFactory.newClient(connectString, 5000, 10000, new RetryNTimes(10, 200));
        this.client.start();
    }

    @Override
    public String create(String uid, boolean isPublish) {
        String gid = UUID.fastUUID().toString();
        try {
            this.client.create().withMode(CreateMode.PERSISTENT).forPath(this.rootPath + "/" + gid, isPublish ? new byte[]{1}:new byte[]{0});

        } catch (Exception e) {
            LOG.error("create group occur error.", e);
            return "";
        }
        return gid;
    }

    @Override
    public void join(String uid, String gid) {

    }

    @Override
    public boolean quit(String uid, String gid) {
        return false;
    }

    @Override
    public boolean dissolve(String uid, String gid) {
        return false;
    }

    @Override
    public void invite(String uid, String toId, String gid) {

    }

    @Override
    public boolean addToAdmin(String uid, String toId, String gid) {
        return false;
    }

    @Override
    public boolean removeFromAdmin(String uid, String toId, String gid) {
        return false;
    }

    @Override
    public boolean remove(String uid, String toId, String gid) {
        return false;
    }

    @Override
    public List<String> list(String uid, String gid) {
        return Lists.newArrayList("1", "2", "3", "4");
    }
}
