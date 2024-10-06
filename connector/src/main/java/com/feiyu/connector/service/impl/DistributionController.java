package com.feiyu.connector.service.impl;

import com.feiyu.base.LifeCycle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DistributionController implements LifeCycle {

    @Override
    public void start() throws Exception {
        log.info("distribution controller start election.");
        startElection();
        log.info("distribution controller started.");
    }

    @Override
    public void destroy() throws Exception {
        log.info("destroy distribution controller.");
        destroyElection();
        describeWorker();
    }

    public abstract void subscribeWorker();

    public abstract void describeWorker();

    public abstract void startElection();

    public abstract void destroyElection();
}
