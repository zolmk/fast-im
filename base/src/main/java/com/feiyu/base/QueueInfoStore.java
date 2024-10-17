package com.feiyu.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueueInfoStore {
  private static final Map<String, QueueInfo> queueInfoMap = new ConcurrentHashMap<>();
  private static final Map<Long, QueueInfo> idsMap = new ConcurrentHashMap<>();

  /**
   * 需要保证同一个queueStr多次调用只会返回第一次创建的QueueInfo
   * @param queueStr str
   * @return QueueInfo
   */
  public static QueueInfo create(String queueStr) {
    if (queueInfoMap.containsKey(queueStr)) {
      return queueInfoMap.get(queueStr);
    }
    String[] split = queueStr.split("=");
    QueueInfo queueInfo = new QueueInfo(split[0], split[1]);
    if (queueInfoMap.putIfAbsent(queueStr, queueInfo) == null) {
      // 仅首次放入时会进入到这里
      idsMap.put(queueInfo.getId(), queueInfo);
    }
    return queueInfoMap.get(queueStr);
  }

  /**
   * 通过id获取指定QueueInfo对象
   * @param id id
   * @return QueueInfo
   */
  public static QueueInfo get(long id) {
    return idsMap.get(id);
  }
}
