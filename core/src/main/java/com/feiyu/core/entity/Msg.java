package com.feiyu.core.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 客户端消息表
 * </p>
 *
 * @author julian
 * @since 2024-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Msg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 消息来源
     */
    private Long sender;

    /**
     * 接收者
     */
    private Long receiver;

    /**
     * 消息序列号
     */
    private Long seq;

    /**
     * 消息状态，<0-已删除、0-已接收、1-已送达、2-已读
     */
    private Integer status;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 消息类型，业务用
     */
    private Integer type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
