package com.jhpark.simple_chat_consumer.kafka.dto;

import lombok.Data;

@Data
public class KafkaSessionOfflineSignal {

    private Long userId;
    private String sessionId;
    private String serverIp;

}