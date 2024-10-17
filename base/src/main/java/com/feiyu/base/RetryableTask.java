package com.feiyu.base;

import com.feiyu.base.interfaces.Failover;
import com.feiyu.base.interfaces.Revocable;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class RetryableTask implements Runnable, Revocable, Failover {
  private final int retryCount;
  private final int retryInterval;
  private final TimeUnit retryIntervalTimeUnit;
  private AtomicInteger retryCounter;
  private final ScheduledExecutorService executor;
  private volatile boolean cancel;
  public RetryableTask(int retryCount, int retryInterval, TimeUnit timeUnit, ScheduledExecutorService executor) {
    this.retryCount = retryCount;
    this.retryInterval = retryInterval;
    this.retryIntervalTimeUnit = timeUnit;
    this.retryCounter = new AtomicInteger(0);
    this.executor = executor;
    this.cancel = false;
  }

  @Override
  public void run() {
    if (this.cancel) return;
    if ( retryCounter.incrementAndGet() <= retryCount ) {
      try {
        if (execute()) return;
        log.info("The message failed to be sent and will be retried later.");
      } catch (Exception e) {
        log.error("retryable task error.", e);
      }
      executor.schedule(this, this.retryInterval, this.retryIntervalTimeUnit);
    } else {
      // 到达最大重试次数
      this.failover();
    }
  }

  @Override
  public void revoke() {
    this.cancel = true;
  }

  @Override
  public boolean isRevoked() {
    return this.cancel || retryCounter.get() > retryCount;
  }

  protected abstract boolean execute() throws Exception;

}
