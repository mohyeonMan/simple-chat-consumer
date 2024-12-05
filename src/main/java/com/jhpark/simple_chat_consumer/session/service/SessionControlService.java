package com.jhpark.simple_chat_consumer.session.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_consumer.redis.service.RedisService;
import com.jhpark.simple_chat_consumer.session.dto.UserSessionInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionControlService {

    private static final String USER_PREFIX = "user:";
    
    private final RedisService redisService;


    private String getUserKey(final Long userId) {
        return USER_PREFIX + userId;
    }
    private String getSessionKey(final String serverIp, final String sessionId) {
        return serverIp + ":" + sessionId;
    }


    public void sessionConnected(
            final Long userId,
            final String roomId,
            final String serverIp,
            final String sessionId
    ) {
        final String userKey = getUserKey(userId);
        final String sessionKey = getSessionKey(serverIp, sessionId);

        redisService.save(userKey, sessionKey, roomId);
    }


    public void sessionDisconnected(
        final Long userId, 
        final String serverIp,
        final String sessionId
    ) {
        final String userKey = getUserKey(userId);
        final String sessionKey = getSessionKey(serverIp, sessionId);

        if(!redisService.isExistKey(userKey)) {
            log.error("User is not exist: {}", userKey);
            return;
        }

        redisService.delete(userKey, sessionKey);

    }

    public Map<String, Set<UserSessionInfo>> getUserSessionInfos(final Set<Long> userIds, final String roomId) {
        return userIds.stream()
            .flatMap(userId -> getUserSessionInfos(userId, roomId).entrySet().stream())
            .collect(Collectors.toMap(
                Map.Entry::getKey, // 서버 IP
                Map.Entry::getValue, // UserSessionInfo Set
                (set1, set2) -> { // 중복 키 처리 (서버 IP가 동일한 경우)
                    set1.addAll(set2);
                    return set1;
                }
            ));
    }
    
    public Map<String, Set<UserSessionInfo>> getUserSessionInfos(final Long userId, final String roomId) {
        final String userKey = getUserKey(userId);
    
        return redisService.getHash(userKey).entrySet().stream()
            .filter(entry -> roomId.equals(entry.getValue()))
            .map(entry -> Map.entry(
                extractServerIp(entry.getKey()),
                createUserSessionInfo(userId, entry.getKey())
            ))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
            ));
    }
    
    private UserSessionInfo createUserSessionInfo(final Long userId,final String sessionKey) {
        return UserSessionInfo.builder()
            .userId(userId)
            .sessionIds(Set.of(extractSessionId(sessionKey)))
            .build();
    }
    

    private String extractSessionId(final String sessionKey) {
        return sessionKey.split(":")[1];
    }

    private String extractServerIp(final String sessionKey) {
        return sessionKey.split(":")[0];
    }

}
