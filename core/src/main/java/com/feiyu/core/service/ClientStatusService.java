package com.feiyu.core.service;

/**
 * @author zhufeifei 2024/10/20
 **/


public interface ClientStatusService {
    int getStatus(long cid);
    void setStatus(long cid, int status);
    default boolean offline(long cid) {
        return getStatus(cid) == 0;
    }
    default boolean online(long cid) {
        return getStatus(cid) == 1;
    }
}
