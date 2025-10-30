package com.example.chat.models;



import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection="notifications")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {

    @Id
    private String id;
    @NonNull
    private String sender;
    @NonNull
    private String receiver;
    private String group;
    @NonNull
    private String type;
    private String title;
    private String description;

    @Indexed(expireAfterSeconds=3600)
    @CreatedDate
    private Date created_at;

    @LastModifiedDate   // 👈 auto-updated whenever saved again
    private Date updatedAt;
}
