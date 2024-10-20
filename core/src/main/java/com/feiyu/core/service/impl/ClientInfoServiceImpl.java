package com.feiyu.core.service.impl;

import com.feiyu.core.entity.ClientInfo;
import com.feiyu.core.mappers.ClientInfoMapper;
import com.feiyu.core.service.IClientInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户端信息表 服务实现类
 * </p>
 *
 * @author julian
 * @since 2024-10-20
 */
@Service
public class ClientInfoServiceImpl extends ServiceImpl<ClientInfoMapper, ClientInfo> implements IClientInfoService {

}
