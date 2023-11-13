package com.fy.chatserver.persistent.entity;

import java.util.Date;

/**
 * @author zhufeifei 2023/11/11
 **/

public class UserStateEntity {
    private String uid;
    private boolean online;
    private Date lastDt;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Date getLastDt() {
        return lastDt;
    }

    public void setLastDt(Date lastDt) {
        this.lastDt = lastDt;
    }
}
