package com.jhpark.simple_chat_consumer.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhpark.simple_chat_consumer.message.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
