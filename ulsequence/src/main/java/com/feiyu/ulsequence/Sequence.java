package com.feiyu.ulsequence;

import java.util.Properties;

/**
 * user-level seq 发号系统
 * @param <UID>
 */
public interface Sequence<UID> {
    /**
     * generate serial number for user
     * @param uid the user
     * @return serial number
     */
    Long nexSeq(UID uid);

    static Sequence<Long> getInstance(SequenceConfiguration cfg) {
        return new DefaultSequenceImpl(cfg);
    }
}
