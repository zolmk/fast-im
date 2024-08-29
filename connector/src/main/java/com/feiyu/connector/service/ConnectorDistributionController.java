package com.feiyu.connector.service;

import com.feiyu.connector.config.ZKConfig;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
public class ConnectorDistributionController extends ZooKeeperDistributionController  {
    public ConnectorDistributionController(ZKConfig zkConfig) {
        super(zkConfig);
    }

    @Override
    public void event(Type type, ChildData oldData, ChildData data) {

    }

    // 主节点，负责topic的分配
    public class ControllerTask implements Runnable {
        @Override
        public void run() {
            
        }
    }
}
