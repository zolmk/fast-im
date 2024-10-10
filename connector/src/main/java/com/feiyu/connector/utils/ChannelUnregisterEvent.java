package com.feiyu.connector.utils;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ChannelUnregisterEvent {
  private String uid;
  private long mq;
}
