package com.feiyu.ulsequence.service;

import com.feiyu.ulsequence.Result;
import org.springframework.stereotype.Component;

/**
 * @author zhufeifei 2024/6/6
 **/


public interface ISequenceService {
    Result<?> gen(Long uid);
}
