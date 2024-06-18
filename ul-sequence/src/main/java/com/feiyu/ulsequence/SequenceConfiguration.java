package com.feiyu.ulsequence;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.function.Function;

import static com.feiyu.ulsequence.Constants.*;

/**
 * 序列发号器的配置类
 * @author Zhuff
 */
@Getter
@Setter
@Configuration
public class SequenceConfiguration implements InitializingBean {

    @Value(value = "${ul-sequence.start-uid}")
    private Long uncheckStartUid;
    @Value(value = "${ul-sequence.end-uid}")
    private Long uncheckEndUid;
    @Value(value = "${ul-sequence.grow-step}")
    private Integer uncheckGrowStep;
    @Value(value = "${ul-sequence.segment-size}")
    private Integer uncheckSegmentSize;
    @Value(value = "${ul-sequence.node-id}")
    private String nodeId;


    private Argument<Long> startUid;
    private Argument<Long> endUid;
    private Argument<Integer> growStep;
    private Argument<Integer> segmentSize;
    private Argument<Integer> segmentCnt;
    public SequenceConfiguration() {
    }

    @Bean
    public Sequence<Long> sequence() {
        return new DefaultSequenceImpl(this);
    }
    private static <T> Argument<T> build(Function<Object, T> function, String argName) {
        return new Argument<>(function.apply(argName), argName);
    }

    public static void checkValue(SequenceConfiguration config) {
        if ((config.endUid.val & 1) != 0 || (((config.startUid.val - 1) & 1) != 0) || (config.segmentSize.val & 1) != 0) {
            throw new RuntimeException(String.format("配置错误：%s %s %s 必须都为2的指数", config.endUid, config.startUid, config.segmentSize));
        }

        if ((config.endUid.val - config.startUid.val + 1) % config.segmentSize.val != 0) {
            throw new RuntimeException(String.format("配置错误：%s %s %s，总用户数必须能整除段大小", config.endUid, config.startUid, config.segmentSize));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.startUid = new Argument<>(this.uncheckStartUid, SERVICE_PREFIX + DOT + nodeId + ".start-uid");
        this.endUid = new Argument<>(this.uncheckEndUid, SERVICE_PREFIX + DOT + nodeId + ".end-uid");
        this.growStep = new Argument<>(this.uncheckGrowStep, SERVICE_PREFIX + DOT + nodeId + ".grow-step");
        this.segmentSize = new Argument<>(this.uncheckSegmentSize, SERVICE_PREFIX + DOT + nodeId + ".segment-size");
        checkValue(this);
        this.segmentCnt = new Argument<>((int) ((this.endUid.val - this.startUid.val + 1) / this.segmentSize.val), "segment_cnt");
    }

    @Getter
    @Setter
    static class Argument<T> {
        private T val;
        private String name;
        public Argument(T val, String name) {
            this.val = val;
            this.name = name;
        }

        @Override
        public String toString() {
            return "【" + this.name + ":" + this.val + "】";
        }
    }
}

