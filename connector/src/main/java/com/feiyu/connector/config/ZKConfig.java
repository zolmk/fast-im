package com.feiyu.connector.config;

import lombok.Getter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = true)
@Getter
@ConfigurationProperties("zk")
public class ZKConfig {
    private String connectStr;
    private final String electionPath = "/app/election";
    private final String workerPath = "/app/workers";
    private final int connectTimeout = 5000;
    private final int sessionTimeout = 50000;

    @Bean
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient(connectStr, sessionTimeout, connectTimeout, new RetryNTimes(10, 10*1000));
    }


}
