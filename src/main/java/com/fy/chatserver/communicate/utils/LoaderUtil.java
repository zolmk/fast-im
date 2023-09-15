package com.fy.chatserver.communicate.utils;

import com.fy.chatserver.discovery.ServiceFinder;
import com.fy.chatserver.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhufeifei 2023/9/15
 **/

public class LoaderUtil {
    private static final Logger LOG = LoggerFactory.getLogger(LoaderUtil.class);
    public static Object load(String clazz, String tip) {
        try {
            Class<?> aClass = Class.forName(clazz);
            return aClass.newInstance();
        } catch (ClassNotFoundException e) {
            LOG.error("{} configuration is error, {} class not found.", tip, clazz);
            throw new RuntimeException(e);
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("{} class must have empty constructor.", clazz);
            throw new RuntimeException(e);
        }
    }
}
