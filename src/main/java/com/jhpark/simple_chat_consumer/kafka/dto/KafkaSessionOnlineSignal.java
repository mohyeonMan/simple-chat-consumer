package com.jhpark.simple_chat_consumer.kafka.dto;

import lombok.Data;

@Data
public class KafkaSessionOnlineSignal {

    private Long userId;
    private String sessionId;
    private String roomId;
    private String serverIp;
    
}
