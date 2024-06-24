package com.feiyu.ulsequence;

/**
 * 实现分号段共享存储
 * @author Zhuff
 */
public interface Section <T>{
    /**
     * 当前Setion是否包含给定用户
     * @param uid 用户ID
     * @return boolean
     */
    boolean has(T uid);

    /**
     * 返回允许分配的最大序列号
     * @return seq
     */
    long maxSeq();

    /**
     * 设置允许分配的最大序列号，注意设置前需要获得锁
     * @param maxSeq 最大序列号
     */
    void setMaxSeq(long maxSeq);

    /**
     * 返回给定用户的下一个可用的序列号
     * @param uid user id
     * @return 序列号
     */
    long nextSeq(T uid);

    /**
     * 获取当前段内用户的最小ID
     * @return user id
     */
    Long getFirst();

    /**
     * 获取当前段内用户的最大ID
     * @return user id
     */
    Long getLast();

    /**
     * 尝试增大允许分配的最大序列号
     * 如果需要增大一般为当前最大序列号 + step，注意这里需要持久化
     * @param seq 当前序列号
     */
    void grow(long seq);

    /**
     * 获取步长
     * @return step
     */
    int getStep();

}
