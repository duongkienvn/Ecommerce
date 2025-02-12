package com.project.shopapp.utils.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class RedisUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> T convertValue(Object cachedData, Class<T> targetClass) {
        return objectMapper.convertValue(cachedData, targetClass);
    }
}
