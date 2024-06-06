package com.feiyu.route;

/**
 * 成功和失败的回调函数
 * @param <T> 上下文对象的类型
 */
public interface Callback <T> {
    /**
     * 执行成功时回调
     * @param t t
     */
    void success(T t);

    /**
     * 执行失败时回调
     * @param t t
     */
    void fail(T t);
}
