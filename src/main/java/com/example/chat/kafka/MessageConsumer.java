package com.example.chat.kafka;

import com.example.chat.models.Message;
import com.example.chat.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @KafkaListener(topics = "chat-message", groupId = "chat-app-messages")
    public void consume(Message message) throws JsonProcessingException {
        System.out.println("Kafka consumer");
        messageService.push(message);
    }
}

