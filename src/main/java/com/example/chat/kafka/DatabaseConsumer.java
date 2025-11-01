package com.example.chat.kafka;

import com.example.chat.models.Message;
import com.example.chat.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Service
public class DatabaseConsumer {

    @Autowired
    private MessageService messageService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @KafkaListener(topics = "chat-database", groupId = "chat-app-image")
    public void consume(ConsumerRecord<String, Message> record) {
        Message saved=messageService.save(record.value());
        kafkaProducer.sendMessage("chat-message", record.key(),saved);
    }
}


