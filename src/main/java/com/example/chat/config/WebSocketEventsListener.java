package com.example.chat.config;

import com.example.chat.models.User;
import com.example.chat.service.MessageService;
import com.example.chat.service.NotificationService;
import com.example.chat.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;

@Component
public class WebSocketEventsListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @Autowired
    private UserService userService;

    @EventListener
    public void onConnect(SessionConnectedEvent event) throws JsonProcessingException {
        Principal principle=event.getUser();
        if (principle == null) return;
        String userId=principle.getName();
        List<User> friends=userService.getFriends(userId);
        for (User friend : friends) {
            notificationService.push(userId, friend.getId(), "user is Online", "info", "/topic/connected");
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) throws JsonProcessingException {
        Principal principle=event.getUser();
        if (principle == null) return;
        String userId=principle.getName();
        try {
            System.out.println(userId + " " + simpUserRegistry.getUser(userId).getSessions().size());
        }
        catch (Exception e){
            List<User> friends=userService.getFriends(userId);
            for (User friend : friends)
                notificationService.push(userId, friend.getId(), "user is offline", "info", "/topic/disconnected");
        }
    }
}
