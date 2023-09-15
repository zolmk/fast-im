package com.fy.chatserver.discovery;

import java.util.Properties;

/**
 * @author zhufeifei 2023/9/12
 **/

public class LocalServiceProvider implements ServiceProvider {
    @Override
    public ServiceFinder newInstance(Properties properties) {
        LocalServiceFinderConfig config = new LocalServiceFinderConfig();
        config.load(properties);
        return new LocalServiceFinder(config);
    }
}
