package com.example.chat.repository;

import com.example.chat.models.Notifications;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notifications,String> {

    List<Notifications> findBySender(String sender );
    List<Notifications> findByReceiver( String receiver );
    List<Notifications> deleteByReceiver( String receiver );
}
