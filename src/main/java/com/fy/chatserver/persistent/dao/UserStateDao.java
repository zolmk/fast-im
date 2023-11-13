package com.fy.chatserver.persistent.dao;

import com.fy.chatserver.persistent.entity.UserStateEntity;

/**
 * 管理用户状态表
 * @author zhufeifei 2023/11/11
 **/


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
