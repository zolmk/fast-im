package com.feiyu.ulsequence;

import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 默认号段实现
 * @author Zhuff
 */
public class DefaultSectionImpl implements Section<Long>{
    private final AtomicLongArray slots;
    private final Long firstUid, lastUid;
    private final int step;
    private volatile long maxSeq;
    private final ReentrantLock mainLock;

    private final int segmentLen;

    public DefaultSectionImpl(Long first, Long last, int step, Long maxSeq) {
        this.firstUid = first;
        this.lastUid = last;
        this.segmentLen = (int) (last - first + 1);
        this.step = step;
        this.maxSeq = maxSeq;
        this.mainLock = new ReentrantLock();
        long[] array = new long[segmentLen];
        for (int i = 0; i < segmentLen; i++) {
            array[i] = maxSeq;
        }
        this.slots = new AtomicLongArray(array);
    }

    @Override
    public boolean has(Long uid) {
        return uid >= firstUid && uid <= lastUid;
    }

    @Override
    public long maxSeq() {
        return this.maxSeq;
    }

    @Override
    public void setMaxSeq(long maxSeq) {
        this.maxSeq = maxSeq;
    }

    @Override
    public long nextSeq(Long uid) {
        assert uid >= this.firstUid && uid <= this.lastUid;
        long seq = this.slots.getAndIncrement((int)(uid - this.firstUid));
        grow(seq);
        return seq;
    }

    @Override
    public Long getFirst() {
        return this.firstUid;
    }

    @Override
    public Long getLast() {
        return this.lastUid;
    }

    @Override
    public void grow(long seq) {
        long maxSeq = this.maxSeq;
        if (seq + 1 >= maxSeq) {
            try {
                mainLock.lock();
                if (seq + 1 >= maxSeq) {
                    long nextMaxSeq = maxSeq + this.step;
                    // TODO: 更新数据库中的maxSeq

                    setMaxSeq(nextMaxSeq);
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    @Override
    public int getStep() {
        return this.step;
    }

    @Override
    public String toString() {
        return String.format("[start: %d, end: %d, segmentLen: %d, maxSeq: %d]", this.firstUid, this.lastUid, this.segmentLen, this.maxSeq);
    }
}
