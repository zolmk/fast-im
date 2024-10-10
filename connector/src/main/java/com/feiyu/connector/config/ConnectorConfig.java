package com.feiyu.connector.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接器配置类
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "connector")
public class ConnectorConfig {

  private String id;

  private int bossCount = Runtime.getRuntime().availableProcessors();

  private int workerCount = Runtime.getRuntime().availableProcessors();

  private int acceptPort = 9977;

  private int soBacklog = 5000;

  private String mqAllocator = "range";

  private String messageReceiver = "kafka";

  private String mqChooser = "round";

  private List<String> topicList = new ArrayList<>();
}
