package com.feiyu.connector.runner;

import com.feiyu.connector.utils.NamedBeanProvider;
import com.feiyu.connector.service.impl.ConnectorDistributionController;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 控制器Runner
 * 如果称为主节点，则负责队列分配等任务
 */
@Component
@ConditionalOnBean(NamedBeanProvider.class)
@Order(value = 7777)
public class ControllerRunner implements ApplicationRunner {

  private final ConnectorDistributionController distributionController;
  public ControllerRunner(ConnectorDistributionController distributionController) {
    this.distributionController = distributionController;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    distributionController.start();
  }
}
