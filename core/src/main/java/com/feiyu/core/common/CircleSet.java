package com.feiyu.core.common;

import java.util.Iterator;

/**
 * @author zhufeifei 2023/9/9
 **/

public interface CircleSet<T> {
    void add(T t);
    T next();
    int size();
    Iterator<T> iter();
}
