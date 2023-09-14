package com.fy.chatserver.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author zhufeifei 2023/9/9
 **/

public class SyncCircleSet <T> implements CircleSet<T> {
    private final List<T> list;
    private final AtomicInteger idx;

    public SyncCircleSet() {
        this(0, null);
    }
    public SyncCircleSet(int num, Supplier<T> supplier) {
        this.list = Collections.synchronizedList(new ArrayList<>());
        this.idx = new AtomicInteger(0);
        for (int i = 0; i < num; i++) {
            this.add(supplier.get());
        }
    }

    public void add(T t) {
        this.list.add(t);
    }

    public T next() {
        int size = size();
        if (size == 0) return null;
        return this.list.get(idx.getAndIncrement() % size);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Iterator<T> iter() {
        return this.list.iterator();
    }
}
