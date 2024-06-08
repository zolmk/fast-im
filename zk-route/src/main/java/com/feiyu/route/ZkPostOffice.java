package com.feiyu.route;

import com.feiyu.common.R;
import com.feiyu.common.Result;
import com.feiyu.interfaces.ISequenceService;
import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author zhufeifei 2024/6/8
 **/

@Component
public class ZkPostOffice implements PostOffice<MessageLite, Long, Object, Channel>{
    @DubboReference
    private ISequenceService sequenceService;

    @Override
    public void login(Long aLong, Channel channel) {

    }

    @Override
    public void logout(Long aLong) {

    }

    @Override
    public Optional<Long> nextMsgSeq(Long aLong) {
        Result<?> gen = sequenceService.gen(aLong);
        if (gen.getCode() == R.CODE_FAIL) {
            return Optional.empty();
        }
        return Optional.of((Long) gen.getData());
    }

    @Override
    public List<MessageLite> getHistory(Long u1, Long u2, Long u1StartSeq, Long u1EndSeq, Long u2StartSeq, Long u2EndSeq) {
        return null;
    }

    @Override
    public void ack(Long msgId, int state) {

    }

    @Override
    public LetterBox<MessageLite, Object> getLetterBox(Long aLong) {
        return null;
    }
}
