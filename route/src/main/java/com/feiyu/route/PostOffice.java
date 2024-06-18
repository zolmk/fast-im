package com.feiyu.route;

import com.feiyu.base.UserEventSubscriber;

import java.util.Optional;


/**
 * post office
 * @author Zhuff
 * @param <MSG> 消息的类型
 * @param <UID> 用户ID的类型
 * @param <N> 通知的类型
 * @param <CH> IO通道的类型
 */
public interface PostOffice <MSG, UID, N, CH> extends UserEventSubscriber<UID, CH> {

    /**
     * 查询用户下一个可用的消息序列号
     * @param uid 用户ID
     * @return 消息序列号
     */
    Optional<Long> nextMsgSeq(UID uid);

    /**
     * 确认指定的消息
     * @param msgId 消息ID
     * @param state 要确认的状态
     */
    void ack(Long msgId, int state);

    /**
     * 获取指定用户的信箱
     * @param uid 用户ID
     * @return 用户信箱
     */
    LetterBox<MSG, N> getLetterBox(UID uid);

    /**
     * 个人信箱
     * @author Zhuff
     * @param <MSG> 消息的类型
     * @param <N> 通知的类型
     */
    interface LetterBox<MSG, N> {
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

        /**
         * 信箱状态
         * @return State
         */
        LetterBoxStateEnum state();
    }

    enum LetterBoxStateEnum {
        /** 正常 */
        NORMAL,
        /** 正在创建 */
        CREATING,
        /** 已关闭 */
        CLOSED;
    }
}
