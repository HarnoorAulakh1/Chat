package com.example.chat.service;

import com.example.chat.models.Message;
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

    public void publishChat(Message message) throws JsonProcessingException {
        publish("chat",message);
    }

    public void publishFriendReq(Message message) throws JsonProcessingException {
        publish("FriendReq",message);
    }

    public void publish(String channel, Message message) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        String json=mapper.writeValueAsString(message);
        redisTemplate.convertAndSend(channel, json);
    }
}
