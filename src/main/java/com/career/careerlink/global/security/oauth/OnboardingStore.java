package com.career.careerlink.global.security.oauth;

import com.career.careerlink.global.redis.RedisProps;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OnboardingStore {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper om;
    private final RedisProps props;

    private String codeKey(String code) {
        return props.onboardingKeyPrefix() + "code:" + code;
    }
    private String providerKey(String provider, String providerUserId) {
        return props.onboardingKeyPrefix() + "provider:" + provider + ":" + providerUserId;
    }

    public void saveReplacingPrevious(OnboardingPayload payload) {
        final String code = payload.getCode();
        final String cKey = codeKey(payload.getCode());
        final String pKey = providerKey(payload.getProvider(), payload.getProviderUserId());
        final Duration ttl = Duration.ofSeconds(props.onboardingTtlSeconds());

        final String json;
        try {
            json = om.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        redisTemplate.execute(new SessionCallback<Void>() {
            @Override
            public <K, V> Void execute(RedisOperations<K, V> operations) {
                ValueOperations<String, String> ops = (ValueOperations<String, String>) operations.opsForValue();

                // 1) 기존 code 조회
                String oldCode = ops.get(pKey);
                if (oldCode != null) {
                    // 2) 기존 codeKey 삭제(기존 링크/세션 무효화)
                    operations.delete((K) codeKey(oldCode));
                }

                // 3) 새 payload 저장
                ops.set(cKey, json, ttl);

                // 4) providerKey → 새 code 갱신
                ops.set(pKey, code, ttl);

                return null;
            }
        });
    }

    public Optional<OnboardingPayload> getByCode(String code) {
        try {
            String json = redisTemplate.opsForValue().get(codeKey(code));
            if (json == null) return Optional.empty();
            return Optional.of(om.readValue(json, OnboardingPayload.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /** 검증 성공 시 payload 소비(삭제). providerKey도 정리 */
    public void consumeAndCleanup(String code, String provider, String providerUserId) {
        redisTemplate.execute(new SessionCallback<Void>() {
            @Override
            public <K, V> Void execute(RedisOperations<K, V> operations) {
                operations.delete((K) codeKey(code));
                operations.delete((K) providerKey(provider, providerUserId));
                return null;
            }
        });
    }
}
