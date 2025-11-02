package com.example.chat.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    private String sender;
    private String receiver;

    private String content;
    private String group;

    private String image;

    private FileInfo file;

    @CreatedDate
    private Date created_At;

    private List<read> isRead = new ArrayList<>();

    @Transient
    private User senderEm;

    @Transient
    private User receiverEm;
}


@Data
class read{
    private User user;
    private String readAt;
}