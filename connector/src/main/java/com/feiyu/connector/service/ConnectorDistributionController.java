package com.feiyu.connector.service;

import com.feiyu.connector.config.ZKConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class ConnectorDistributionController extends ZooKeeperDistributionController implements InitializingBean, ApplicationContextAware {

    private Map<String, MQAllocator> allocatorMap;
    private ApplicationContext applicationContext;

    public ConnectorDistributionController(ZKConfig zkConfig) {
        super(zkConfig);
        allocatorMap = new HashMap<>();
    }

    @Override
    public void event(Type type, ChildData oldData, ChildData data) {
        switch (type) {
            case NODE_CHANGED: {

            } break;
            case NODE_CREATED: {
                // realloc queue
                // 分配队列给其他 Worker

            } break;
            case NODE_DELETED: {
                // nothing
                log.info("node removed. {}", oldData.getPath());
            } break;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, MQAllocator> beansOfType = this.applicationContext.getBeansOfType(MQAllocator.class);
        this.allocatorMap.putAll(beansOfType);
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }


    // 主节点，负责topic的分配
    public class ControllerTask implements Runnable {
        @Override
        public void run() {
            
        }
    }
}
