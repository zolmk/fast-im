package com.feiyu.connector.utils;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChannelRegisterEvent {
  private final long uid;
  private final Channel channel;
  private final long mq;
}
