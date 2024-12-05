package com.jhpark.simple_chat_consumer.kafka.dto;

import java.util.Set;

import lombok.Data;

@Data
public class KafkaChatMessage {
    private Long senderId;
    private Set<Long> userIds;
    private String message;
    private String roomId;
}