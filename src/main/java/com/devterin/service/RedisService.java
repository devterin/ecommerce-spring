package com.devterin.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void save(String key, String value);
    Boolean isTokenBlacklisted(String token);
    void saveToBlacklist(String key, String value, int timeout, TimeUnit timeUnit);
}
