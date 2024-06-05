package com.feiyu.ulsequence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 构建序列号发号器
 * @author zhufeifei 2024/6/6
 **/

public class SequenceBuilder {
    public static Sequence<Long> build() {
        Properties properties = new Properties();
        try(InputStream is = Sequence.class.getClassLoader().getResourceAsStream("ulsequence.properties");) {
            properties.load(is);
            SequenceConfiguration cfg = SequenceConfiguration.load("node-1", properties);
            return Sequence.getInstance(cfg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
