package com.feiyu.core.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 客户端状态表
 * </p>
 *
 * @author julian
 * @since 2024-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ClientStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端ID
     */
    private Long clId;

    /**
     * 状态，0-离线、>1-在线
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
