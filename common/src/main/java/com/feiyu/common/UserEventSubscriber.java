package com.feiyu.common;

/**
 * 用户事件订阅者
 * @author zhufeifei 2024/6/8
 **/


public interface UserEventSubscriber <UID, CH>{
    void login(UID uid, CH channel);
    void logout(UID uid);
}
