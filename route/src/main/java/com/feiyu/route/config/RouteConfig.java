package com.feiyu.route.config;


import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@ConfigurationProperties(prefix = "route")
@Getter
@Setter
public class RouteConfig {
  private String queueType = "kafka";

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
    return stringRedisTemplate;
  }

  @Bean
  public JsonMapper jsonMapper() {
    return new JsonMapper();
  }
}
