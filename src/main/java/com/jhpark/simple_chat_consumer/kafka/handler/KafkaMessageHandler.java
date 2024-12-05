package com.jhpark.simple_chat_consumer.kafka.handler;

import java.util.Map;
import java.util.Set;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_consumer.broadcast.service.BroadcastRequestService;
import com.jhpark.simple_chat_consumer.common.util.ObjectMapperUtil;
import com.jhpark.simple_chat_consumer.kafka.dto.KafkaChatMessage;
import com.jhpark.simple_chat_consumer.kafka.dto.KafkaSessionOfflineSignal;
import com.jhpark.simple_chat_consumer.kafka.dto.KafkaSessionOnlineSignal;
import com.jhpark.simple_chat_consumer.session.dto.UserSessionInfo;
import com.jhpark.simple_chat_consumer.session.service.SessionControlService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageHandler {

    private static final String MESSAGE_TOPIC = "chat-message";
    private static final String SESSION_ONLINE_TOPIC = "session-online";
    private static final String SESSION_OFFLINE_TOPIC = "session-offline";

    private final ObjectMapperUtil objectMapperUtil;
    private final SessionControlService sessionControlService;
    private final BroadcastRequestService broadcastRequestService;

    /**
     * Redis에 세션 정보 저장
     * 사용자id와 세션으로 검색해와서, roomId가 다르면,
     * 수정해서 넣어주고, 기존 구독내용은 제거하도록 요청.
     * 세션 당 하나의 방에만 허락해야하기 때문.
     * key: sessionId, value: {userId, serverIp, roomId}
     */
    @KafkaListener(topics = SESSION_ONLINE_TOPIC)
    public void sessionOnlineSignalConsume(final String message) {
        log.info("SESSION ONLINE SIGNAL: {}", message);

        final KafkaSessionOnlineSignal signal = objectMapperUtil.readValue(message, KafkaSessionOnlineSignal.class);

        sessionControlService.sessionConnected(
                signal.getUserId(),
                signal.getRoomId(),
                signal.getServerIp(),
                signal.getSessionId());

    }

    /**
     * Redis에 세션 정보 삭제
     * sessionId key로 삭제 요청
     */
    @KafkaListener(topics = SESSION_OFFLINE_TOPIC)
    public void sessionOfflineSignalConsume(final String message) {
        log.info("SESSION OFFLINE SIGNAL: {}", message);

        final KafkaSessionOfflineSignal signal = objectMapperUtil.readValue(message, KafkaSessionOfflineSignal.class);
        sessionControlService.sessionDisconnected(signal.getUserId(), signal.getServerIp(), signal.getSessionId());
    }

    // CHAT MESSAGE:
    // {"senderId":2,"userIds":[5,4,3,2,1],"roomId":"1","message":"test"}
    @KafkaListener(topics = MESSAGE_TOPIC)
    public void chatMessageConsume(String message) {
        log.info("CHAT MESSAGE: {}", message);

        final KafkaChatMessage chatMessage = objectMapperUtil.readValue(message, KafkaChatMessage.class);

        // serverIp 별로 요청할 UserSessionInfo 그룹핑
        final Map<String, Set<UserSessionInfo>> serverIpUserSessionInfosMap = sessionControlService.getUserSessionInfos(chatMessage.getUserIds(),chatMessage.getRoomId());

        //브로드캐스트 요청
        serverIpUserSessionInfosMap.forEach((serverIp, userSessionInfos) -> {
            log.info("Server IP: {}", serverIp);
            userSessionInfos.forEach(userSessionInfo -> log.info("\t\tUser Session Info: {}", userSessionInfo));
        });

        serverIpUserSessionInfosMap.entrySet().parallelStream().forEach(entry -> {
            
            final String serverIp = entry.getKey();
            final Set<UserSessionInfo> userSessionInfos = entry.getValue();

            broadcastRequestService.sendRequest(
                chatMessage.getSenderId(),
                serverIp,
                userSessionInfos,
                chatMessage.getRoomId(),
                chatMessage.getMessage());

        });

    }
    

}
