package com.feiyu.core;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhufeifei 2023/11/16
 **/

@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages = "com.feiyu.core.mappers")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
