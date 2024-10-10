package com.feiyu.connector.service.impl;

import com.feiyu.base.QueueInfo;
import com.feiyu.base.eventbus.EventBus;
import com.feiyu.base.eventbus.Subscribe;
import com.feiyu.base.proto.Messages;
import com.feiyu.connector.service.MQChooser;
import com.feiyu.connector.utils.WorkerQueuesChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Slf4j
@Component(value = "roundMQChooser")
@ConditionalOnProperty(name = "connector.mq-chooser", havingValue = "round", matchIfMissing = true)
public class RoundMQChooser implements MQChooser {

  private final ReentrantReadWriteLock lock;
  private volatile AtomicInteger cursor;
  private List<Long> queueIds;

  public RoundMQChooser() {
    EventBus.register(this);
    this.lock = new ReentrantReadWriteLock();
    this.cursor = new AtomicInteger(0);
    this.queueIds = new ArrayList<>();
  }

  @Override
  public long choice(Messages.ClientInfo clientInfo) {
    long qid = -1;
    try {
      this.lock.readLock().lock();
      if (queueIds.isEmpty()) {
        return qid;
      }
      qid = queueIds.get(this.cursor.getAndIncrement());
      if (this.cursor.get() == queueIds.size()) {
        this.cursor.compareAndSet(this.queueIds.size(), 0);
      }
    }finally {
      this.lock.readLock().unlock();
    }
    log.info("client {} choice qid {}", clientInfo.toString(), qid);
    return qid;
  }

  @Subscribe
  public void queueChange(WorkerQueuesChangeEvent event) {
    try {
      this.lock.writeLock().lock();
      Set<Long> ids = new HashSet<>(this.queueIds);
      if (event.getMount() != null) {
        for(QueueInfo qi : event.getMount()) {
          ids.add(qi.getId());
        }
      }
      if (event.getUnmount() != null) {
        for(QueueInfo qi : event.getUnmount()) {
          ids.remove(qi.getId());
        }
      }
      this.queueIds = new ArrayList<>(ids);
      Collections.sort(this.queueIds);
    } finally {
      this.lock.writeLock().unlock();
    }
    log.info("queue changed to {}", this.queueIds);
  }

  @Override
  public String name() {
    return "roundMQChooser";
  }
}
