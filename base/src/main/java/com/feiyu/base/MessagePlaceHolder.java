package com.feiyu.base;

import lombok.Data;

/**
 * 消息占位符
 * @author Zhuff
 */
@Data
public class MessagePlaceHolder {
    /** 消息ID */
    private Long msgId;
    /** 服务ID */
    private String serviceId;
}
