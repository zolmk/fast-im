package com.feiyu.connector.service;

import com.feiyu.base.Named;

import java.util.List;
import java.util.Map;

/**
 * @author zhufeifei 2024/10/6
 **/


public interface MQAllocator extends Named {
    Map<String, List<String>> alloc(List<String> workers, List<String> topics);
}
