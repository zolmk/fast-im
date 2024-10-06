package com.feiyu.connector.service.impl;

import com.feiyu.connector.service.MQAllocator;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 范围分配器
 * @author zhufeifei 2024/10/6
 **/

@Component(value = "range")
public class RangeMQAllocator implements MQAllocator {
    /**
     * 范围分配，默认分配策略
     * @param workers worker列表
     * @param topics topic列表
     * @return map
     */
    @Override
    public Map<String, List<String>> alloc(List<String> workers, List<String> topics) {
        Collections.sort(topics);
        Collections.sort(workers);
        Map<String, List<String>> topicMap = new HashMap<>();
        int i = 0;
        int bs = topics.size() / workers.size();
        for(String wid : workers) {
            topicMap.put(wid, topics.subList(i, Math.min(i+bs, topics.size())));
            i += bs;
        }
        return topicMap;
    }
}
