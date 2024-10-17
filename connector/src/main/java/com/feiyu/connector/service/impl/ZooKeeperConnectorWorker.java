package com.feiyu.connector.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.feiyu.base.QueueInfo;
import com.feiyu.base.QueueInfoStore;
import com.feiyu.base.eventbus.EventBus;
import com.feiyu.base.utils.NetUtil;
import com.feiyu.connector.config.ConnectorConfig;
import com.feiyu.connector.config.ZKConfig;
import com.feiyu.connector.service.ConnectorWorker;
import com.feiyu.connector.service.MessageConsumer;
import com.feiyu.connector.utils.NamedBeanProvider;
import com.feiyu.connector.utils.WorkerQueuesChangeEvent;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于zk的连接器工作节点的实现
 */
@Slf4j
@Component
@Order(value = 1000)
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
    Set<String> oldTopics = readTopics(oldData);
    Set<String> newTopics = readTopics(newData);

    Set<String> mount = Sets.difference(newTopics, oldTopics);
    Set<String> unmount = Sets.difference(oldTopics, newTopics);

    MessageConsumer messageConsumer = NamedBeanProvider.getSingleton(MessageConsumer.class);
    List<QueueInfo> mountList = null;
    if ( ! mount.isEmpty()) {
      mountList = mount.stream().map(QueueInfoStore::create).collect(Collectors.toList());
      messageConsumer.mount(mountList);
    }
    List<QueueInfo> unmountList = null;
    if ( ! unmount.isEmpty()) {
      unmountList = unmount.stream().map(QueueInfoStore::create).collect(Collectors.toList());
      messageConsumer.unmount(unmountList);
    }

    if ( ! mount.isEmpty() || ! unmount.isEmpty()) {
      EventBus.post(new WorkerQueuesChangeEvent(mountList, unmountList));
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
