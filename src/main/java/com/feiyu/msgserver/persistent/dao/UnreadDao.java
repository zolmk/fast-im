package com.feiyu.msgserver.persistent.dao;

import com.feiyu.msgserver.persistent.entity.UnreadEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 管理用户未读消息表
 * @author zolmk
 */
@Mapper
public interface UnreadDao {

    /**
     * 获取某个用户未读消息，并将消息从未读消息表中删除，然后存入用户历史消息表中
     * @param uid 用户id
     * @return 未读消息列表
     */
    List<UnreadEntity> getAndSwap(String uid);

    /**
     * 保存未读消息
     * @param entity 消息
     */
    void save(UnreadEntity entity);

    /**
     * 判断用户是否存在未读消息
     * @param uid 用户id
     */
    boolean exists(String uid);

}
