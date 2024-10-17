package com.feiyu.connector.service;

import com.feiyu.base.interfaces.Named;
import com.feiyu.base.QueueInfo;
import io.netty.channel.Channel;

import java.util.List;

/**
 * 消息接收器抽象层
 */
public interface MessageConsumer extends Named {
  /**
   * 挂载队列到当前节点
   * @param mqs 消息队列列表
   */
  void mount(List<QueueInfo> mqs);

  /**
   * 解挂队列
   * @param mqs 队列列表
   */
  void unmount(List<QueueInfo> mqs);

  /**
   * 将用户注册到mq上
   * 注册用户到MQ的目的是为了接收消息，以及当解挂载队列时，
   * 需要将队列上注册的用户连接重置。
   *
   * @param uid 用户ID
   * @param channel 用户channel
   * @param mqId 队列ID
   */
  void register(long uid, Channel channel, long mqId);

  /**
   * 取消用户注册
   * @param uid 用户ID
   * @param mq 队列名称
   */
  void unregister(long uid, long mq);
}
