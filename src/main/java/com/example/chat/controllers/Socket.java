package com.example.chat.controllers;
import com.example.chat.models.Message;
import lombok.Data;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class Socket {

    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        System.out.println("message is:"+message);
        return message;
    }

    @MessageMapping("/connect")
    public void check(ConnectDto message) {
        //sessions.put(message.getUsername(),session);
        System.out.println("socket connected");
    }
}

@Data
class ConnectDto{
    private String username;
}
