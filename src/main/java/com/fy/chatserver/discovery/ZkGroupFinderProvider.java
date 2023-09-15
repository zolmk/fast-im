package com.fy.chatserver.discovery;

import java.util.Properties;

/**
 * @author zhufeifei 2023/9/15
 **/

public class ZkGroupFinderProvider implements ServiceProvider{
    @Override
    public GroupFinder newInstance(Properties properties) {
        String connectString = properties.getProperty("chat-server.group-finder.zk.connect-string");
        String rootPath = properties.getProperty("chat-server.group-finder.zk.finder.root-path", "/apps/chat-server");
        return new ZkGroupFinder(rootPath, connectString);
    }
}
