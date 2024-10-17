package com.feiyu.connector.utils;

import com.feiyu.base.QueueInfo;
import com.feiyu.connector.config.mq.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class KafkaConsumersFactory {
  /**
   * 根据传入的队列信息创建消费者
   * @param queueInfos
   * @param kafkaConfig
   * @return
   */
  public static List<KafkaConsumerWrapper> create(List<QueueInfo> queueInfos, KafkaConfig kafkaConfig) {
    List<KafkaConsumerWrapper> consumers = new ArrayList<>();
    for (QueueInfo queueInfo : queueInfos) {
      List<KafkaConsumer<String, byte[]>> kafkaConsumers = createKafkaConsumer(queueInfo, kafkaConfig);
      for (KafkaConsumer<String, byte[]> kafkaConsumer : kafkaConsumers) {
        consumers.add(new KafkaConsumerWrapper( kafkaConsumer, queueInfo.getId()));
      }
    }
    return consumers;
  }

  /**
   * 根据队列信息创建与Topic分区数相等的KafkaConsumer
   * @param queueInfo
   * @param kafkaConfig
   * @return
   */
  private static List<KafkaConsumer<String, byte[]>> createKafkaConsumer(QueueInfo queueInfo, KafkaConfig kafkaConfig) {
    List<KafkaConsumer<String, byte[]>> ans = new ArrayList<>();
    String topicName = queueInfo.getQueueName();
    Properties props = new Properties();
    props.put("bootstrap.servers", queueInfo.getConnectStr());
    try(AdminClient adminClient = AdminClient.create(props)) {
      TopicDescription description = describeTopic(adminClient, topicName);
      if (description == null) {
        CreateTopicsResult topics = adminClient.createTopics(Collections.singleton(new NewTopic(topicName, kafkaConfig.getPartitionNumber(), (short) kafkaConfig.getReplicasNumber())));
        try {
          topics.all().get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException("create topic failed");
        }
        // 如果创建失败，则直接抛错
        description = describeTopic(adminClient, topicName);
        if (description == null) {
          throw new RuntimeException("create topic failed");
        }
      }
      // 创建与分区数相等的消费者
      for (int i = 0; i < description.partitions().size(); i++) {
        Properties consumerProps = new Properties();
        consumerProps.putAll(kafkaConfig.kafkaConsuemrProperties());
        consumerProps.put("bootstrap.servers", queueInfo.getConnectStr());
        KafkaConsumer<String, byte[]> kafkaConsumer = new KafkaConsumer<>(consumerProps);
        kafkaConsumer.subscribe(Collections.singletonList(topicName));
        ans.add(kafkaConsumer);
      }
    }
    return ans;
  }

  /**
   * 获取topic细节，如果为空，则表示topic不存在
   * @param adminClient
   * @param topicName
   * @return
   */
  private static TopicDescription describeTopic(AdminClient adminClient, String topicName) {
    try {
      DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singleton(topicName));
      return describeTopicsResult.all().get().get(topicName);
    } catch (UnknownTopicOrPartitionException | ExecutionException | InterruptedException e) {
      log.error("describeTopic failed", e);
      return null;
    }
  }
}
