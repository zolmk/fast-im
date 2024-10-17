package com.feiyu.ulsequence.service.impl;

import com.feiyu.interfaces.idl.ISequenceService;
import com.feiyu.interfaces.idl.SequenceReq;
import com.feiyu.interfaces.idl.SequenceRsp;
import com.feiyu.ulsequence.Sequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 序列号发号服务实现类
 * @author zhufeifei 2024/6/6
 **/
@Component
@Slf4j
@DubboService(loadbalance = "consistenthash")
public class SequenceServiceImpl implements ISequenceService {

    private final Sequence<Long> sequence;

    public SequenceServiceImpl(Sequence<Long> sequence) {
        this.sequence = sequence;
    }

    @Override
    public SequenceRsp gen(SequenceReq uid) {
        Long seq = -1L;
        try {
            seq = this.sequence.nexSeq(uid.getUid());
        } catch (Exception e) {
            //TODO
            log.error("seq generate error", e);
        }
        return SequenceRsp.newBuilder().setSeq(seq).build();
    }

    @Override
    public CompletableFuture<SequenceRsp> genAsync(SequenceReq request) {
        return CompletableFuture.supplyAsync(() -> this.gen(request));
    }
}
