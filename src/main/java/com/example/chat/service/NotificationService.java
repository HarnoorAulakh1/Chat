package com.example.chat.service;


import com.example.chat.controllers.Notification;
import com.example.chat.models.Notifications;
import com.example.chat.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RedisPublisher redisPublisher;

    public void push(String sender,String receiver,String description,String type) throws JsonProcessingException {
        Notifications notification=Notifications.builder().type(type).receiver(receiver).sender(sender).description(description).build();
        Notifications saved =notificationRepository.save(notification);
        //System.out.println("notification saved= "+saved.getId());
        redisPublisher.publish("notifications",saved);
    }
}
