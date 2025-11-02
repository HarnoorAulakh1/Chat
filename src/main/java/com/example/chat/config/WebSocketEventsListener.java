package com.example.chat.config;

import com.example.chat.service.MessageService;
import com.example.chat.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class WebSocketEventsListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) throws JsonProcessingException {
        Principal principle=event.getUser();
        String userId=principle.getName();
        try {
            System.out.println(userId + " " + simpUserRegistry.getUser(userId).getSessions().size());
        }
        catch (Exception e){
            notificationService.push("",userId,"All Users Disconnected","disconnect");
        }
    }
}
