package com.feiyu.route;

/**
 * 个人信箱
 * @param <MSG> 消息的类型
 * @param <N> 通知的类型
 */
public interface LetterBox<MSG, N> {
    /**
     * 向用户信箱中投递消息
     * @param msg 消息体
     * @param callback 回调函数
     */
    void deliver(MSG msg, Callback<MSG> callback);

    /**
     * 向用户信箱中投递通知消息
     * @param n 通知
     */
    void notify(N n);
}
