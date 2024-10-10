package com.feiyu.connector.utils;

import lombok.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonExecutor {
  private static final ExecutorService executorService = new ThreadPoolExecutor(4, Runtime.getRuntime().availableProcessors() * 4, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadFactory() {
    AtomicInteger cnt = new AtomicInteger(0);
    @Override
    public Thread newThread(@NonNull Runnable r) {
      Thread t = new Thread(r);
      t.setDaemon(true);
      t.setName("CommonExecutor-Thread-" + cnt.getAndIncrement());
      return t;
    }
  }, new ThreadPoolExecutor.CallerRunsPolicy());

  public static ExecutorService getExecutor() {
    return executorService;
  }
}
