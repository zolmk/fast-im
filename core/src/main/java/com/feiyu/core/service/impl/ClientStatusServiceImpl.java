package com.feiyu.core.service.impl;

import com.feiyu.core.entity.ClientStatus;
import com.feiyu.core.mappers.ClientStatusMapper;
import com.feiyu.core.service.IClientStatusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户端状态表 服务实现类
 * </p>
 *
 * @author julian
 * @since 2024-10-20
 */
@Service
public class ClientStatusServiceImpl extends ServiceImpl<ClientStatusMapper, ClientStatus> implements IClientStatusService {

}
