package com.devterin.service.impl;

import com.devterin.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    @Override
    public Boolean isTokenBlacklisted(String token) {
        String key = "access_token:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    @Override
    public void saveToBlacklist(String key, String value, int timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

}
