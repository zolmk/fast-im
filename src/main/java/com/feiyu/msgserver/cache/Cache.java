package com.feiyu.msgserver.cache;

import java.io.Closeable;

/**
 * @author zhufeifei 2023/11/11
 **/

public interface Cache extends Closeable {
    /**
     * 判断指定key下是否有val记录
     * @param key 键
     * @param val 值
     * @return boolean
     */
    boolean hasRecord(String key, String val);

    /**
     * 在key下对val进行记录
     * @param key 键
     * @param val 值
     */
    void record(String key, String val);

    /**
     * 获取指定键的值
     * @param key 键
     * @return 值
     */
    String getVal(String key);

    /**
     * 设置指定键的值
     * @param key 键
     * @param val 值
     */
    void setVal(String key, String val);
}
