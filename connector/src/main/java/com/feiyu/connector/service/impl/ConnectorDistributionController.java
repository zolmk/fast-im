package com.feiyu.connector.service.impl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feiyu.connector.config.ConnectorConfig;
import com.feiyu.connector.config.ZKConfig;
import com.feiyu.connector.service.MQAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ConnectorDistributionController extends ZooKeeperDistributionController implements InitializingBean, ApplicationContextAware {

  private Map<String, MQAllocator> allocatorMap;
  private ApplicationContext applicationContext;
  private final ConnectorConfig connectorConfig;
  private final JsonMapper jsonMapper;
  private final CuratorFramework client;

  public ConnectorDistributionController(ConnectorConfig connectorConfig, ZKConfig zkConfig, JsonMapper jsonMapper) {
    super(connectorConfig.getId(), zkConfig);
    this.jsonMapper = jsonMapper;
    allocatorMap = new HashMap<>();
    this.connectorConfig = connectorConfig;
    this.client = zkConfig.curatorFramework();
  }

  @Override
  public void event(Type type, ChildData oldData, ChildData data) {
    switch (type) {
      case NODE_CREATED: {
        if (!data.getPath().startsWith(zkConfig.getWorkerPath() + "/")) {
          return;
        }
        // realloc queue
        // 分配队列给其他 Worker
        log.info("node create: {}", new String(data.getData()));
        MQAllocator allocator = allocatorMap.get(connectorConfig.getMqMallocStrategy());
        Map<String, List<String>> range = allocator.alloc(workers(), connectorConfig.getTopicList());
        updateWorkerQueue(range);
      }
      break;
      case NODE_DELETED: {
        // nothing
        log.info("node removed. {}", data.getPath());
      }
      break;
    }
  }

  private void updateWorkerQueue(Map<String, List<String>> dataMap)  {
    for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
      String workerPath = entry.getKey();
      String workerData = new String(curatorCache.get(workerPath).get().getData(), StandardCharsets.UTF_8);
      if (StringUtils.isEmpty(workerData)) {
        workerData = "{}";
      }
      ArrayNode arrayNode = jsonMapper.createArrayNode();
      for(String s : entry.getValue()) {
        arrayNode.add(s);
      }
      String newData = "";
      try {
        ObjectNode root = (ObjectNode) jsonMapper.readTree(workerData);
        root.set("topics", arrayNode);
        newData = root.toPrettyString().toLowerCase(Locale.ROOT);
        this.client.setData().forPath(workerPath, newData.getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
        log.error("update worker {} error, data {}", workerPath, newData,  e);
      }
    }
  }


  private List<String> workers() {
    return curatorCache.stream().map(ChildData::getPath).filter(s->s.startsWith(zkConfig.getWorkerPath()+"/")).collect(Collectors.toList());
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
