package com.example.chat.kafka;

import com.example.chat.models.Message;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ImageConsumer {

    @KafkaListener(topics = "chat-messages", groupId = "chat-app-group")
    public void consume(Message message) {
        System.out.println("Received message: " + message);
    }
}


