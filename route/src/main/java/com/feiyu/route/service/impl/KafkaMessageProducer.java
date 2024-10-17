package com.feiyu.route.service.impl;


import com.fasterxml.jackson.databind.json.JsonMapper;
import com.feiyu.base.QueueInfo;
import com.feiyu.base.QueueInfoStore;
import com.feiyu.route.config.KafkaConfig;
import com.feiyu.route.util.KafkaProducerWrapper;
import com.feiyu.route.util.Producer;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Objects;

import static com.feiyu.base.Constants.*;

@Component
@ConditionalOnProperty(name = "route.queueType", havingValue = "kafka", matchIfMissing = true)
public class KafkaMessageProducer extends AbstractMessageProducer {

  private final RedisTemplate<String, String> redisTemplate;
  private final JsonMapper jsonMapper;
  private final KafkaConfig kafkaConfig;

  public KafkaMessageProducer(RedisTemplate<String, String> redisTemplate, JsonMapper jsonMapper, KafkaConfig kafkaConfig) {
    this.redisTemplate = redisTemplate;
    this.jsonMapper = jsonMapper;
    this.kafkaConfig = kafkaConfig;
  }

  @Override
  protected Producer createProducer(long to) throws Exception {
    QueueInfo queueInfo;
    String queueInfoStr = redisTemplate.opsForValue().get(QUEUE_INFO_PREFIX + to);
    if (StringUtils.isEmpty(queueInfoStr)) {
      throw new IllegalArgumentException(String.format("%d queue info is empty", to));
    }
    queueInfo = Objects.requireNonNull(jsonMapper.readValue(queueInfoStr, QueueInfo.class));
    return new KafkaProducerWrapper(queueInfo, this.kafkaConfig);
  }

  @Override
  public String name() {
    return "kafkaMessageProducer";
  }
}
