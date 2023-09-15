package com.fy.chatserver.discovery;

import org.apache.curator.shaded.com.google.common.collect.Lists;

import java.util.List;

/**
 * @author zhufeifei 2023/9/15
 **/

public class ZkGroupFinder implements GroupFinder {
    private final String rootPath;
    private final String connectString;

    public ZkGroupFinder(String rootPath, String connectString) {
        this.rootPath = rootPath;
        this.connectString = connectString;
    }

    @Override
    public String create(String uid, boolean isPublish) {
        return null;
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
