package com.feiyu.interfaces;


import com.feiyu.common.Result;

/**
 * @author zhufeifei 2024/6/6
 **/


public interface ISequenceService {
    Result<?> gen(Long uid);
}
