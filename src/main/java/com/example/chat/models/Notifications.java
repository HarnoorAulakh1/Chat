package com.example.chat.models;



import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Data
public class Notifications {

    @Id
    private String id;
    @NonNull
    @DBRef
    private User sender;
    @NonNull
    @DBRef
    private User receiver;
    private Group group;
    private String type;
    private String title;
    private String description;

    @Indexed(expireAfterSeconds=3600)
    private Date created_at;

    @LastModifiedDate   // 👈 auto-updated whenever saved again
    private Date updatedAt;

    private Notifications() {
        this.created_at = new Date();
    }



}
