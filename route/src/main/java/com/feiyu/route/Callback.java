package com.feiyu.route;

/**
 * 成功和失败的回调函数
 * @param <T> 上下文对象的类型
 */
public interface Callback <T> {
    void success(T t);
    void fail(T t);
}
