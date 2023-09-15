package com.fy.chatserver.discovery;

import java.util.Properties;

/**
 * @author zhufeifei 2023/9/9
 **/

public interface ServiceProvider {
    Object newInstance(Properties properties);
}
