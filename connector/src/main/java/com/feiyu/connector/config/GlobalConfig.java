package com.feiyu.connector.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 全局配置类
 */

@Configuration
public class GlobalConfig {
  @Bean
  public JsonMapper jsonMapper() {
    return new JsonMapper();
  }
}
