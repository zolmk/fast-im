package com.feiyu.msgserver.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhufeifei 2023/9/9
 **/

public interface CircleSet<T> {
    void add(T t);
    T next();
    int size();
    Iterator<T> iter();
}
