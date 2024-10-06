package com.feiyu.connector.service;

import java.util.List;
import java.util.Map;

/**
 * @author zhufeifei 2024/10/6
 **/


public interface MQAllocator {
    Map<String, List<String>> alloc(List<String> workers, List<String> topics);
}
