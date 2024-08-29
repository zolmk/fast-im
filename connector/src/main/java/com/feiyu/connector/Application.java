package com.feiyu.connector;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhufeifei 2024/6/2
 **/
@SpringBootApplication
@EnableDubbo
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
