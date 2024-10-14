package com.feiyu.connector.service.impl;

import com.feiyu.connector.config.ZKConfig;
import com.feiyu.connector.enums.ElectionState;
import com.feiyu.connector.service.ElectionService;
import com.feiyu.connector.service.ElectionStateListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

@Slf4j
public abstract class ZooKeeperDistributionController extends DistributionController implements CuratorCacheListener, ElectionStateListener {
    private final ElectionService electionService;
    protected final ZKConfig zkConfig;
    private volatile boolean isLeader = false;
    protected CuratorCache curatorCache;

    public ZooKeeperDistributionController(String id, ZKConfig zkConfig) {
        this.zkConfig = zkConfig;
        this.electionService = new CuratorElectionService(id, zkConfig.getElectionPath(), zkConfig.curatorFramework(), this);
    }

    @Override
    public void change(ElectionState state) {
        switch (state) {
            case READY:break;
            case LEADER: {
                if (!isLeader) {
                    isLeader = true;
                    // 强制成功
                    while (true) {
                        try {
                            subscribeWorker();
                            break;
                        } catch (Exception e) {
                            log.error("ZooKeeperDistributionController occur error while subscribe worker.", e);
                        }
                        try {
                            describeWorker();
                        } catch (Exception ignore) {
                        }
                    }
                }
            } break;
            case FOLLOWER: {
                if (isLeader) {
                    isLeader = false;
                    try {
                        describeWorker();
                    } catch (Exception e) {
                        log.error("ZooKeeperDistributionController occur error while describe worker.", e);
                    }
                }
            }break;
        }
    }

    @Override
    public void subscribeWorker() {
        log.info("Controller subscribe worker. path: {}", this.zkConfig.getWorkerPath());
        curatorCache = CuratorCache.build(this.zkConfig.curatorFramework(), this.zkConfig.getWorkerPath());
        curatorCache.start();
        curatorCache.listenable().addListener(this);
    }

    @Override
    public void describeWorker() {
        log.info("Controller describe worker.");
        if (this.curatorCache != null) {
            curatorCache.listenable().removeListener(this);
            curatorCache.close();
            curatorCache = null;
        }
    }

    @Override
    public void startElection() {
        log.info("Starting election leader.");
        try {
            electionService.start();
        } catch (Exception e) {
            log.error("A error occurred while starting to elect leader. Error: {}", e.getMessage(), e);
        }
    }

    @Override
    public void destroyElection() {
        log.info("Destroy election service.");
        try {
            electionService.destroy();
        } catch (Exception e) {
            log.error("A error occurred while destroy the ElectionService. Error: {}", e.getMessage(), e);
        }
    }
}
