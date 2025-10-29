package com.example.chat.controllers;
import com.example.chat.models.Message;
import com.example.chat.service.RedisPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class Socket {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private RedisPublisher redisPublisher;


    @MessageMapping("/send")
    public void send(Message message) throws JsonProcessingException {
        System.out.println("message is:"+message.getSender());
        redisPublisher.publishChat(message);
    }

    @MessageMapping("/FriendReq")
    public void friend_req(Message message) throws JsonProcessingException {
        System.out.println("message is:"+message.getSender());
        redisPublisher.publishFriendReq(message);
    }

    @MessageMapping("/connect")
    public void check(ConnectDto message) {
        System.out.println("socket connected");
    }

}

@Data
class ConnectDto{
    private String username;
}
