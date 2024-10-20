package com.feiyu.core.service.impl;

import com.feiyu.core.service.ClientStatusService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author zhufeifei 2024/10/21
 **/

@Service
public class CachedClientStatusServiceImpl implements ClientStatusService {
    private final static String USER_STATUS = "user_status:";
    private final StringRedisTemplate stringRedisTemplate;
    public CachedClientStatusServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public int getStatus(long cid) {
        String v = stringRedisTemplate.opsForValue().get(USER_STATUS + cid);
        if (StringUtils.isBlank(v)) return 0;
        return Integer.parseInt(v);
    }

    @Override
    public void setStatus(long cid, int status) {
        stringRedisTemplate.opsForValue().set(USER_STATUS + cid, String.valueOf(status));
    }
}
