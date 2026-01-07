package com.example.chat.repository;

import com.example.chat.models.Chatroom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Chatroom, String> {

    Optional<Chatroom> findById(String id);
}
