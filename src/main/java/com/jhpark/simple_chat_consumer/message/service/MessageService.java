package com.jhpark.simple_chat_consumer.message.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jhpark.simple_chat_consumer.message.entity.Message;
import com.jhpark.simple_chat_consumer.message.repository.MessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Async
    public void saveMessageAsync(
        final Long roomId,
        final Long senderId,
        final String content
    ){
        log.info("SAVE MESSAGE : roomId={}, senderId={}, content={}",roomId,senderId,content);
        messageRepository.save(
            Message.builder().
                roomId(roomId)
                .senderId(senderId)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build());
    }

}
