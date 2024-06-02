package com.feiyu.msgserver.discovery;

import java.util.Properties;

/**
 * @author zhufeifei 2023/9/9
 **/

public class ZkServiceFinderProvider implements ServiceProvider {
    @Override
    public ServiceFinder newInstance(Properties properties) {
        String connectString = properties.getProperty("chat-server.service-finder.zk.connect-string");
        String rootPath = properties.getProperty("chat-server.service-finder.zk.finder.root-path", "/apps/chat-server");
        return new ZkServiceFinder(rootPath, connectString);
    }
}
