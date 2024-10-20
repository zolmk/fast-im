package com.feiyu.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 客户端信息表
 * </p>
 *
 * @author julian
 * @since 2024-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ClientInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端信息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户端ID
     */
    private Long clId;

    /**
     * 客户端设备名称
     */
    private String dvcName;

    /**
     * 设备序列号
     */
    private String dvcSn;

    /**
     * 消息漫游开关
     */
    private Boolean sync;

    /**
     * 最新序列号
     */
    private Long seq;

    /**
     * 同步界限，同步设置值天数内的消息
     */
    private Integer bounds;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
