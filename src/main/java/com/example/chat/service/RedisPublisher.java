package com.example.chat.service;

import com.example.chat.models.Message;
import com.example.chat.models.Notifications;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisPublisher {

    private final RedisTemplate<String,String> redisTemplate;

    public RedisPublisher(RedisTemplate<String,String> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public void publish(String channel, Object message) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        String json=mapper.writeValueAsString(message);
        redisTemplate.convertAndSend(channel, json);
    }
}
