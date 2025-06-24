package com.career.careerlink.global.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String userid, String refreshToken, long duration) {
        redisTemplate.opsForValue().set(userid, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    public String findByLoginId(String userid) {
        return redisTemplate.opsForValue().get(userid);
    }

    public void deleteByEmail(String userid) {
        redisTemplate.delete(userid);
    }
}
