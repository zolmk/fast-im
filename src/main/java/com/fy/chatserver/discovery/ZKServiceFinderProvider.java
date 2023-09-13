package com.fy.chatserver.discovery;

import java.util.Properties;

/**
 * @author zhufeifei 2023/9/9
 **/

public class ZKServiceFinderProvider implements ServiceFinderProvider{
    @Override
    public ServiceFinder newInstance(Properties properties) {
        String connectString = properties.getProperty("chat-server.service-finder.zk.connectString");
        String rootPath = properties.getProperty("chat-server.service-finder.zk.finder.rootPath", "/apps/chat-server");
        return new ZKServiceFinder(rootPath, connectString);
    }
}
