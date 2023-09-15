package com.fy.chatserver.common;
import com.fy.chatserver.enums.ComponentStatus;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhufeifei 2023/9/9
 **/


public abstract class Dispatcher<T> implements Closeable, Runnable {

    public Dispatcher(int queueCapacity, Logger logger, int order) {
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.running = new AtomicBoolean(true);
        this.status = ComponentStatus.NOT_START;
        this.LOG = logger;
        this.createOrder = order;
    }

    protected final LinkedBlockingQueue<T> queue;
    protected Thread runThread = null;
    protected final AtomicBoolean running;
    protected ComponentStatus status;
    protected final Logger LOG;
    protected final int createOrder;


    @Override
    public void run() {
        this.initThread();
        this.status = ComponentStatus.RUNNING;
        LOG.info("{} started.", name());
        while (this.running.get()) {
            try {
                T protocol = this.queue.poll(1000, TimeUnit.MILLISECONDS);
                if (protocol == null) {
                    continue;
                }
                oneLoop(protocol);
            } catch (InterruptedException e) {
                LOG.error(name(), e);
            }
        }
        this.status = ComponentStatus.CLOSED;
    }

    protected abstract void oneLoop(T t) throws InterruptedException;

    protected void initThread() {
        this.runThread = Thread.currentThread();
        this.runThread.setName(String.format("%s-dispatcher-thread-%d",this.namePrefix(), this.createOrder));
    }

    public String name() {
        return this.runThread.getName();
    }

    public boolean offer(T protocol) {
        try {
            return this.status == ComponentStatus.RUNNING && this.queue.offer(protocol, 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.error("the offer thread be interrupted.", e);
            return false;
        }
    }

    public ComponentStatus getStatus() {
        return this.status;
    }

    @Override
    public void close() throws IOException {
        this.queue.clear();
        this.running.set(false);
    }

    public abstract String namePrefix();
}
