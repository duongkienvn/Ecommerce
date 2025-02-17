package com.project.shopapp.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheClient<K, F, V> extends BaseRedisService<K, F, V> {
    public RedisCacheClient(RedisTemplate<K, V> redisTemplate) {
        super(redisTemplate);
    }

    public boolean isUserTokenInWhiteList(String userId, String tokenFromRequest) {
        String tokenFromRedis = (String) this.get((K) ("whitelist:" + userId));
        return tokenFromRedis != null && tokenFromRedis.equals(tokenFromRequest);
    }
}
