package com.jhpark.simple_chat_consumer.session.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSessionInfo {

    private Long userId;
    private Set<String> sessionIds;
    
}
