package com.example.wallet_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {


    private final RedisTemplate<String, String> redisTemplate;

    public boolean setNx(String key, Long expiryTime) {
        try {
            if (expiryTime != null)
                return redisTemplate.opsForValue().setIfAbsent(key, "1", expiryTime, TimeUnit.SECONDS);
            return redisTemplate.opsForValue().setIfAbsent(key, "1");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean set(String key, String value, Long expiryTime) {
        try {
            redisTemplate.opsForValue().set(key, value, expiryTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean removeKey(String key) {
        try {
            return redisTemplate.opsForValue().getOperations().delete(key);
        } catch (Exception e) {
            return false;
        }
    }
}
