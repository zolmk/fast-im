package com.feiyu.ulsequence.service.impl;

import com.feiyu.common.R;
import com.feiyu.common.Result;
import com.feiyu.interfaces.ISequenceService;
import com.feiyu.ulsequence.Sequence;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * @author zhufeifei 2024/6/6
 **/
@Component
@DubboService
public class SequenceServiceImpl implements ISequenceService {

    private final Sequence<Long> sequence;

    public SequenceServiceImpl(Sequence<Long> sequence) {
        this.sequence = sequence;
    }

    @Override
    public Result<?> gen(Long uid) {
        Long seq = -1L;
        try {
            seq = this.sequence.nexSeq(uid);
        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }
        return R.success(seq);
    }
}
