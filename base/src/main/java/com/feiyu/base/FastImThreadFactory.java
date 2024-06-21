package com.feiyu.base;

import lombok.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂类.
 *
 * @author Zhuff
 */
public class FastImThreadFactory implements ThreadFactory {
  private final String usage;
  private final boolean isDaemon;
  private final AtomicInteger cnt;

  public FastImThreadFactory(String usage, boolean isDaemon) {
    this.usage = usage;
    this.isDaemon = isDaemon;
    cnt = new AtomicInteger(1);
  }

  @Override
  public Thread newThread(@NonNull Runnable r) {
    Thread thread = new Thread(r);
    thread.setDaemon(this.isDaemon);
    thread.setName(threadName());
    return thread;
  }

  private String threadName() {
    return String.format("%s - %d", this.usage, this.cnt.getAndIncrement());
  }
}
