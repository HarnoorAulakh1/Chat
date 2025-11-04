package com.example.chat.service;

import com.example.chat.models.Message;
import com.example.chat.models.RedisMessage;
import com.example.chat.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisSubscriberChat {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void onMessage(String message, String channel) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        RedisMessage<Message> json1=mapper.readValue(message, new TypeReference<RedisMessage<Message>>() {});
        Message json= json1.getPayload();
        String destination=json1.getDestination();
        if (json.getSender() != null) {
            Optional<User> user1=userService.findById(json.getSender());
            if(user1.isPresent())
                json.setSenderEm(user1.get());
        }
        if (json.getReceiver() != null) {
            Optional<User> user1=userService.findById(json.getReceiver());
            if(user1.isPresent())
                json.setReceiverEm(user1.get());
        }
        simpMessagingTemplate.convertAndSendToUser(json.getReceiver(),destination,json);
        if(destination.equals("/topic/messages")) {
            simpMessagingTemplate.convertAndSendToUser(json.getSender(), "/topic/preview", json);
            simpMessagingTemplate.convertAndSendToUser(json.getReceiver(), "/topic/preview", json);
        }
    }
}
