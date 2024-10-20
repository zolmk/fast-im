package com.feiyu.core.service.impl;

import com.feiyu.core.entity.Msg;
import com.feiyu.core.mappers.MsgMapper;
import com.feiyu.core.service.IMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户端消息表 服务实现类
 * </p>
 *
 * @author julian
 * @since 2024-10-20
 */
@Service
public class MsgServiceImpl extends ServiceImpl<MsgMapper, Msg> implements IMsgService {

}
