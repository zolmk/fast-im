package com.fy.chatserver.persistent.config;

import com.fy.chatserver.persistent.dao.UserStateDao;
import com.fy.chatserver.persistent.entity.UserStateEntity;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author zolmk
 */
public class MybatisConfig {
    public static void main(String[] args) {
        try(InputStream resourceAsStream = MybatisConfig.class.getResourceAsStream("/mybatis.xml")) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
            UserStateDao mapper = sqlSessionFactory.openSession(true).getMapper(UserStateDao.class);
            UserStateEntity entity = new UserStateEntity();
            entity.setUid("124234");
            entity.setOnline(true);
            entity.setLastDt(new Date());

            mapper.addIfAbsent(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
