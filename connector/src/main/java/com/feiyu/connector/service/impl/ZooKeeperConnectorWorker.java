package com.feiyu.connector.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feiyu.base.utils.NetUtil;
import com.feiyu.connector.config.ConnectorConfig;
import com.feiyu.connector.config.ZKConfig;
import com.feiyu.connector.service.ConnectorWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 基于zk的连接器工作节点的实现
 */
@Slf4j
@Component
public class ZooKeeperConnectorWorker implements ConnectorWorker {
  private CuratorCache cache;
  private final CuratorFramework client;
  private final ZKConfig zkConfig;
  private final ConnectorConfig connectorConfig;
  private final ObjectMapper objectMapper;
  public ZooKeeperConnectorWorker(ZKConfig zkConfig, ConnectorConfig connectorConfig, ObjectMapper objectMapper) {
    this.zkConfig = zkConfig;
    this.client = zkConfig.curatorFramework();
    this.connectorConfig = connectorConfig;
    this.objectMapper = objectMapper;
  }

  @Override
  public void start() throws Exception {
    register();
  }

  @Override
  public void register() throws Exception {
    String workerPath = zkConfig.getWorkerPath() + "/" + connectorConfig.getId();
    Stat stat = client.checkExists().forPath(workerPath);
    if (stat != null) {
      log.error("Worker {} already exists!", connectorConfig.getId());
    }
    // 创建临时worker节点，并监听
    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(workerPath, createWorkerData());
    cache = CuratorCache.build(client, workerPath);
    cache.start();
    cache.listenable().addListener(this);
  }

  @Override
  public void destroy() throws Exception {
    if (cache != null) {
      cache.close();
    }
  }

  @Override
  public void event(Type type, ChildData oldData, ChildData newData) {
    log.info("path: {} data: {}", newData.getPath(), new String(newData.getData(), StandardCharsets.UTF_8));
    //TODO 双向绑定
  }

  private byte[] createWorkerData() {
    ObjectNode root = objectMapper.createObjectNode();
    root.put("id", connectorConfig.getId());
    root.put("ip", NetUtil.getAddress());
    return root.toPrettyString().getBytes(StandardCharsets.UTF_8);
  }
}
