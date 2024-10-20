package com.feiyu.base.proto;

/**
 * @author zhufeifei 2024/10/21
 **/


public enum MsgStatus {
    /**
     * 已接收
     */
    RECEIVED(0),
    /**
     * 已投递/送达
     */
    DELIVERED(1),
    /**
     * 已读
     */
    READ(2),
    /**
     * 撤回
     */
    REVOKE(-1);
    private final int code;
    MsgStatus(int c) {
        this.code = c;
    }
    public int code() {
        return this.code;
    }
}
