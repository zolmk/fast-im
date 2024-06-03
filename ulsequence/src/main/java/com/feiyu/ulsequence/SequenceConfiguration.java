package com.feiyu.ulsequence;

import lombok.Getter;
import lombok.Setter;

import java.util.Properties;
import java.util.function.Function;

import static com.feiyu.ulsequence.Constants.*;

@Getter
public class SequenceConfiguration {
    private Argument<Long> startUid;
    private Argument<Long> endUid;
    private Argument<Integer> growStep;
    private Argument<Integer> startSegmentNumber;
    private Argument<Integer> segmentSize;
    private Argument<Integer> segmentCnt;
    private SequenceConfiguration() {}

    public static SequenceConfiguration load(String nodeId, Properties properties) {
        Function<String, Long> longFunction = (Function<String, Long>) s -> Long.parseLong(properties.getProperty(s));
        Function<String, Integer> intFunction = (Function<String, Integer>) s -> Integer.parseInt(properties.getProperty(s));

        SequenceConfiguration config = new SequenceConfiguration();
        config.startUid = build(longFunction, SERVICE_PREFIX + DOT + nodeId + ".start-uid");
        config.endUid = build(longFunction, SERVICE_PREFIX + DOT + nodeId + ".end-uid");
        config.growStep = build(intFunction, SERVICE_PREFIX + DOT + nodeId + ".grow-step");
        config.startSegmentNumber = build(intFunction, SERVICE_PREFIX + DOT + nodeId + ".start-segment-number");
        config.segmentSize = build(intFunction, SERVICE_PREFIX + DOT + nodeId + ".segment-size");
        checkValue(config);
        config.segmentCnt = new Argument<>((int) ((config.endUid.val - config.startUid.val + 1) / config.segmentSize.val), "segment_cnt");
        return config;
    }

    private static <T> Argument<T> build(Function<String, T> function, String argName) {
        return new Argument<>(function.apply(argName), argName);
    }

    public static void checkValue(SequenceConfiguration config) {
        if ((config.endUid.val & 0x1) != 0 || (((config.startUid.val - 1) & 0x1) != 0) || (config.segmentSize.val & 0x1) != 0) {
            throw new RuntimeException(String.format("配置错误：%s %s %s 必须都为2的指数", config.endUid, config.startUid, config.segmentSize));
        }

        if ((config.endUid.val - config.startUid.val + 1) % config.segmentSize.val != 0) {
            throw new RuntimeException(String.format("配置错误：%s %s %s，总用户数必须能整除段大小", config.endUid, config.startUid, config.segmentSize));
        }
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

