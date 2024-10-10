package com.feiyu.connector.config;

import com.feiyu.connector.service.MQChooser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
