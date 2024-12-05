package com.jhpark.simple_chat_consumer.common.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ObjectMapperUtil {

    private final ObjectMapper objectMapper;

    public String writeValueAsString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write object as string", e);
        }
    }

    public <T> T readValue(final String content, final Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read value", e);
        }
    }

    public <T> T readValue(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON with TypeReference", e);
        }
    }
    
}
