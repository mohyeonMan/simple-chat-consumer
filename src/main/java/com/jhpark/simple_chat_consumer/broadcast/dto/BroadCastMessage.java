package com.jhpark.simple_chat_consumer.broadcast.dto;

import java.util.Set;

import com.jhpark.simple_chat_consumer.session.dto.UserSessionInfo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BroadCastMessage {

    private Long senderId;
    private Set<UserSessionInfo> userSessionInfos;
    private Long roomId;
    private String message;
    
}

