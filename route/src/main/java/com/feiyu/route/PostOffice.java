package com.feiyu.route;

import com.feiyu.common.UserEventSubscriber;
import java.util.List;
import java.util.Optional;


/**
 * post office
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
     * 获取用户1和用户2的离线或者历史消息
     * @param u1 用户1
     * @param u2 用户2
     * @param u1StartSeq 用户1起始序号
     * @param u1EndSeq 用户1结束序号
     * @param u2StartSeq 用户2开始序号
     * @param u2EndSeq 用户2结束序号
     * @return 消息列表
     */
    List<MSG> getHistory(UID u1, UID u2, Long u1StartSeq, Long u1EndSeq, Long u2StartSeq, Long u2EndSeq);

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
}
