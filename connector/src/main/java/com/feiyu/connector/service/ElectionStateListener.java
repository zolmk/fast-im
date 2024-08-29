package com.feiyu.connector.service;

import com.feiyu.connector.enums.ElectionState;



public interface ElectionStateListener {
    /**
     * 选举状态更新.
     *
     * @param state
     */
    void change(ElectionState state);
}
