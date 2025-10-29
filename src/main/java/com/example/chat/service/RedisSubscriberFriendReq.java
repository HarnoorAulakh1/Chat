package com.example.chat.service;

import com.example.chat.models.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriberFriendReq {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void onMessage(String message, String channel) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        Message json=mapper.readValue(message,Message.class);
        json.setContent("You received a friend request from "+json.getSender());
        System.out.println("Friend Req message: "+json.getSender()+" "+json.getReceiver()+" "+json.getContent() + " from channel: " + channel);
        simpMessagingTemplate.convertAndSendToUser(json.getReceiver(),"/topic/FriendReq",json);
    }
}
