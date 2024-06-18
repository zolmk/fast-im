package com.feiyu.ulsequence;


/**
 * user-level seq 发号系统
 * @author Zhuff
 * @param <UID>
 */
public interface Sequence<UID> {
    /**
     * generate serial number for user
     * @param uid the user
     * @return serial number
     */
    Long nexSeq(UID uid);

    /**
     * 根据配置信息获取 Sequence 实例
     * @param cfg config
     * @return Sequence
     */
    static Sequence<Long> getInstance(SequenceConfiguration cfg) {
        return new DefaultSequenceImpl(cfg);
    }
}
