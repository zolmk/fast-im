package com.feiyu.ulsequence;


import java.util.Arrays;
import java.util.List;

/**
 * singleton
 */
public final class DefaultSequenceImpl implements Sequence<Long>{

    private final Section<Long>[] sections;
    private final int segmentShift;
    private final int segMask;
    private final SequenceConfiguration cfg;

    @SuppressWarnings("unchecked")
    public DefaultSequenceImpl(SequenceConfiguration configuration) {
        this.cfg = configuration;
        this.sections = new Section[configuration.getSegmentCnt().getVal()];
        this.segmentShift = Integer.numberOfTrailingZeros(configuration.getSegmentSize().getVal());
        this.segMask = configuration.getSegmentCnt().getVal() - 1;
        this.initSection();
    }

    private void initSection() {
        Section<Long>[] sections = this.sections;
        SequenceConfiguration cfg = this.cfg;
        long first, end, maxSeq;
        first = this.cfg.getStartUid().getVal();
        end = this.cfg.getEndUid().getVal();
        maxSeq = 0L;
        int segmentSize = cfg.getSegmentSize().getVal();
        int step = cfg.getGrowStep().getVal();
        for (int i = 0; i < sections.length; i++) {
            Section<Long> section = new DefaultSectionImpl(first, first + segmentSize - 1, step, maxSeq);
            sections[i] = section;
            first += segmentSize;
        }
    }

    @Override
    public Long nexSeq(Long uid) {
        return sections[pos(uid)].nextSeq(uid);
    }

    private int pos(Long uid) {
        long u = uid - cfg.getStartUid().getVal();
        u >>>= this.segmentShift;
        return (int) (u & this.segMask);
    }

    @Override
    public String toString() {
        return "DefaultSequenceImpl{" +
                "sections=" + Arrays.toString(sections) +
                '}';
    }
}
