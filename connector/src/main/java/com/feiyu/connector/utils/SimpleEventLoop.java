package com.feiyu.connector.utils;

import com.feiyu.base.interfaces.RunOnce;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 事件循环器的简单实现
 */
@Slf4j
public class SimpleEventLoop implements Runnable, Closeable {
  private final List<RunOnce> runOnces;
  private final Object lock = new Object();
  private volatile boolean running = true;
  public SimpleEventLoop() {
    runOnces = new ArrayList<>();
  }

  public SimpleEventLoop(@NonNull final List<RunOnce> runOnces) {
    this.runOnces = new ArrayList<>(runOnces);
  }

  @Override
  public void run() {
    while (running) {
      synchronized (lock) {
        Iterator<RunOnce> iterator = runOnces.iterator();
        while (iterator.hasNext()) {
          RunOnce runOnce = iterator.next();
          if (runOnce.isRevoked()) {
            iterator.remove();
            try {
              runOnce.close();
            } catch (IOException e) {
              log.error("close RunOnce error.", e);
            }
            continue;
          }
          try {
            runOnce.runOnce();
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
        }
        if (runOnces.isEmpty()) {
          running = false;
        }
      }
      Thread.yield();
    }
  }

  public boolean add(RunOnce runOnce) {
    synchronized (lock) {
      if (!running) return false;
      runOnces.add(runOnce);
    }
    return true;
  }

  public void remove(RunOnce runOnce) {
    synchronized (lock) {
      runOnces.remove(runOnce);
    }
  }

  @Override
  public void close() throws IOException {
    this.running = false;
  }
}
