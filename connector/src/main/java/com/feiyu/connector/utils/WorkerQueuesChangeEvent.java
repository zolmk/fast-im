package com.feiyu.connector.utils;

import com.feiyu.base.QueueInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkerQueuesChangeEvent {
  private List<QueueInfo> mount;
  private List<QueueInfo> unmount;
}
