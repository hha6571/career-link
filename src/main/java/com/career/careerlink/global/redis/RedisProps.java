package com.career.careerlink.global.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.redis")
public record RedisProps(String onboardingKeyPrefix, long onboardingTtlSeconds) {}