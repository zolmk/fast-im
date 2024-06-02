package com.feiyu.msgserver.persistent.dao;

import com.feiyu.msgserver.persistent.entity.UserStateEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理用户状态表
 * @author zhufeifei 2023/11/11
 **/


@Mapper
public interface UserStateDao {

    /**
     * 如果不存在记录则添加，如果存在则更新
     * @param entity 用户状态信息
     */
    void addIfAbsent(UserStateEntity entity);


    /**
     * 获取用户状态信息
     * @param uid 用户id
     * @return entity 用户状态信息
     */
    UserStateEntity get(String uid);

}
