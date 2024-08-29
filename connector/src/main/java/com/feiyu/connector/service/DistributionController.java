package com.feiyu.connector.service;

import com.feiyu.base.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DistributionController implements LifeCycle {

    private static final Logger log = LoggerFactory.getLogger(DistributionController.class);

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
