package com.feiyu.connector.config;


import lombok.Getter;
import lombok.Setter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * zk配置类
 */

@Getter
@Setter
@Configuration(proxyBeanMethods = true)
@ConfigurationProperties(prefix = "connector.zk")
public class ZKConfig {
    private String connectStr;
    private final String electionPath = "/apps/connector/election";
    private final String workerPath = "/apps/connector/workers";
    private final int connectTimeout = 5000;
    private final int sessionTimeout = 50000;

    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectStr, sessionTimeout, connectTimeout, new RetryNTimes(10, 10 * 1000));
        curatorFramework.start();
        return curatorFramework;
    }
}
