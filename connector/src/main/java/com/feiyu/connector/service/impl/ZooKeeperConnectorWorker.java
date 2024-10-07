package com.feiyu.connector.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feiyu.base.utils.NetUtil;
import com.feiyu.connector.config.ConnectorConfig;
import com.feiyu.connector.config.ZKConfig;
import com.feiyu.connector.service.ConnectorWorker;
import com.feiyu.connector.service.MessageReceiver;
import com.feiyu.connector.utils.NamedBeanProvider;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
  private final NamedBeanProvider namedBeanProvider;

  public ZooKeeperConnectorWorker(ZKConfig zkConfig, ConnectorConfig connectorConfig, ObjectMapper objectMapper, NamedBeanProvider namedBeanProvider) {
    this.zkConfig = zkConfig;
    this.client = zkConfig.curatorFramework();
    this.connectorConfig = connectorConfig;
    this.objectMapper = objectMapper;
    this.namedBeanProvider = namedBeanProvider;
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
    Set<String> oldTopics = readTopics(oldData);
    Set<String> newTopics = readTopics(newData);

    Set<String> mount = Sets.difference(newTopics, oldTopics);
    Set<String> unmount = Sets.difference(oldTopics, newTopics);

    MessageReceiver messageReceiver = namedBeanProvider.matchPrefix(connectorConfig.getMessageReceiver(), MessageReceiver.class);

    if ( ! mount.isEmpty()) {
      messageReceiver.mount(new ArrayList<>(mount));
    }

    if ( ! unmount.isEmpty()) {
      messageReceiver.unmount(new ArrayList<>(unmount));
    }
  }

  /**
   * 读取topic列表
   * @param childData 节点数据
   * @return topics
   */
  private Set<String> readTopics(ChildData childData) {
    Set<String> topics =  new HashSet<>();
    if (childData == null) {return topics;}
    byte[] data = childData.getData();
    if (data == null) return topics;
    try {
      // 读取当前节点的topic列表
      JsonNode jsonNode = objectMapper.readTree(new String(data, StandardCharsets.UTF_8));
      ArrayNode arrayNode = (ArrayNode) jsonNode.get("topics");
      if (arrayNode == null) return topics;
      for (int i = 0; i < arrayNode.size(); i++) {
        topics.add(arrayNode.get(i).asText());
      }
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
    return topics;
  }

  private byte[] createWorkerData() {
    ObjectNode root = objectMapper.createObjectNode();
    root.put("id", connectorConfig.getId());
    root.put("ip", NetUtil.getAddress());
    return root.toPrettyString().getBytes(StandardCharsets.UTF_8);
  }
}
