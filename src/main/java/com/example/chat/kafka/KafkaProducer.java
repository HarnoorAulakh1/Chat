package com.example.chat.kafka;

import com.example.chat.models.Message;
import io.lettuce.core.dynamic.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, Message> kafkaTemplate;


    public KafkaProducer(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topicName,String key,Message message) {
        kafkaTemplate.send(topicName,key, message);
    }
}
