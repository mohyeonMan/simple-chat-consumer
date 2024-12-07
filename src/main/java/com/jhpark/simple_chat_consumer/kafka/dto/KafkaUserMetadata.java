package com.jhpark.simple_chat_consumer.kafka.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaUserMetadata {
    private Long userId;
    private String sessionId;
    private String serverIp;
}