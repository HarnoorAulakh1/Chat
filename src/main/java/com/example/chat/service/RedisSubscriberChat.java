package com.example.chat.service;

import com.example.chat.models.Message;
import com.example.chat.models.RedisMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriberChat {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void onMessage(String message, String channel) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        RedisMessage<Message> json1=mapper.readValue(message, new TypeReference<RedisMessage<Message>>() {});
        Message json= json1.getPayload();
        String destination=json1.getDestination();
        //System.out.println("Received message: "+json.getSender()+" "+json.getReceiver()+" "+json.getContent() + " from channel: " + channel);
        simpMessagingTemplate.convertAndSendToUser(json.getReceiver(),destination,json);
    }
}
