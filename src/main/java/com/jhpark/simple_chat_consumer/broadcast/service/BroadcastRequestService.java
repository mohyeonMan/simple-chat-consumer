package com.jhpark.simple_chat_consumer.broadcast.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jhpark.simple_chat_consumer.broadcast.dto.BroadCastMessage;
import com.jhpark.simple_chat_consumer.session.dto.UserSessionInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BroadcastRequestService {

    private final WebClient webClient;
    private static final String HTTP_PREFIX = "http://";
    private static final String PORT = ":81";
    private static final String BROADCAST_REQUEST_PATH = "/broadcast"; 

    public void sendBroadcastRequest(
        final Long senderId,
        final String serverIp,
        final Set<UserSessionInfo> userSessionInfos,
        final Long roomId,
        final String message
    ) {

        final String serverUrl = HTTP_PREFIX + serverIp + PORT + BROADCAST_REQUEST_PATH;

        final BroadCastMessage broadCastMessage = BroadCastMessage.builder()
                .senderId(senderId)
                .userSessionInfos(userSessionInfos)
                .roomId(roomId)
                .message(message)
                .build();

        webClient.post().uri(serverUrl)
                .bodyValue(broadCastMessage)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(error -> {
                    log.error("ERROR : {}", error);
                    return Mono.empty();
                })
                .subscribe();
        
        log.debug("SENT BROADCAST REQUEST: {}", broadCastMessage.toString());

    }

}
