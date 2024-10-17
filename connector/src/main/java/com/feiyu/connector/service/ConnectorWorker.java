package com.feiyu.connector.service;

import com.feiyu.base.interfaces.LifeCycle;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

/**
 * 连接器worker节点
 */
public interface ConnectorWorker extends LifeCycle, CuratorCacheListener {
  /**
   * 注册自身到注册中心
   */
  void register() throws Exception;
}
