package com.example.chat.repository;

import com.example.chat.models.Message;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findBySenderAndReceiver(String sender, String receiver, Sort sort);
}