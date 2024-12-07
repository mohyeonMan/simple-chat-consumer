package com.jhpark.simple_chat_consumer.redis.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean save(
            final String key,
            final String hashKey,
            final String value) {

        log.info("REDIS PUT: key={}, hashKey={}, value={}", key, hashKey, value);
        redisTemplate.opsForHash().put(key, hashKey, value);
        return isExistKey(key, hashKey);
    }

    public Map<String, String> getHash(final String key) {
        return redisTemplate.opsForHash().entries(key).entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().toString()));
    }

    public String get(final String key, final String hashKey) {
        return Optional.ofNullable(redisTemplate.opsForHash().get(key, hashKey))
                .map(Object::toString)
                .map(value -> {
                    log.info("REDIS GET: key={}, hashKey={}, value={}", key, hashKey, value);
                    return value;
                })
                .orElse(null); // 키가 없으면 null 반환
    }

    public boolean delete(final String key, final String hashKey) {

        final Long deletedCount = redisTemplate.opsForHash().delete(key, hashKey);

        final boolean deleted = Optional.ofNullable(deletedCount).orElse(0L) > 0;

        log.info("REDIS DELETE: key={}, hashKey={}, deleted={}", key, hashKey, deleted);
        return deleted;
    }

    public boolean isExistKey(final String key, final String hashKey) {
        final boolean exists = Optional.ofNullable(redisTemplate.opsForHash().hasKey(key, hashKey))
                .orElse(false);

        log.info("REDIS EXISTS: key={}, hashKey={}, exists={}", key, hashKey, exists);

        return exists;
    }

    public boolean isExistKey(final String key) {
        final boolean exists = Optional.ofNullable(redisTemplate.hasKey(key))
                .orElse(false);

        log.info("REDIS EXISTS: key={}, exists={}", key, exists);

        return exists;
    }


}
