package com.feiyu.connector.service.impl;

import com.feiyu.connector.enums.ElectionState;
import com.feiyu.connector.service.ElectionService;
import com.feiyu.connector.service.ElectionStateListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

public class CuratorElectionService implements ElectionService {
    private final LeaderLatch leaderLatch;
    private final ElectionStateListener electionStateListener;
    private ElectionState state;

    public CuratorElectionService(String id, String electionPath, CuratorFramework framework, ElectionStateListener electionStateListener) {
        this.leaderLatch = new LeaderLatch(framework, electionPath, id);
        this.electionStateListener = electionStateListener;
        this.state = ElectionState.READY;
        this.electionStateListener.change(ElectionState.READY);
    }

    @Override
    public void start() throws Exception {
        if (this.state == ElectionState.READY) {
            this.electionStateListener.change(ElectionState.ELECTING);
        } else {
            this.electionStateListener.change(ElectionState.REELECTING);
        }
        this.leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                CuratorElectionService.this.state = ElectionState.LEADER;
                CuratorElectionService.this.electionStateListener.change(ElectionState.LEADER);
            }

            @Override
            public void notLeader() {
                CuratorElectionService.this.state = ElectionState.FOLLOWER;
                CuratorElectionService.this.electionStateListener.change(ElectionState.FOLLOWER);
            }
        });
        this.leaderLatch.start();
    }

    @Override
    public void destroy() throws Exception {
        leaderLatch.close();
    }
}
