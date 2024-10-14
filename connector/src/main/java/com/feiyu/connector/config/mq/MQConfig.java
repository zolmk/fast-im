package com.feiyu.connector.config.mq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * 队列配置类
 */

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "mq")
public class MQConfig {
    private String bootstrapStr;
    private List<String> topics;
}
