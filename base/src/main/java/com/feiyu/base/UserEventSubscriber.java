package com.feiyu.base;

/**
 * 用户事件订阅者
 * @author zhufeifei 2024/6/8
 **/


public interface UserEventSubscriber <UID, CH>{
    /**
     * 将自己注册到用户事件发布者.
     *
     * @param publisher 发布者
     */
    void register(UserEventPublisher<UID, CH> publisher);

    /**
     * 用户登录事件来临时调用.
     *
     * @param uid 用户ID
     * @param channel 用户通道
     */
    void login(UID uid, CH channel);

    /**
     * 用户登出事件来临时调用.
     *
     * @param uid 用户ID
     */
    void logout(UID uid);
}
