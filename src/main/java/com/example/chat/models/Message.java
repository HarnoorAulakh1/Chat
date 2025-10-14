package com.example.chat.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
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

    private User sender;
    private User receiver;

    private String content;
    private String group;

    private String image;

    private FileInfo file;

    @CreatedDate
    private Date created_At;

    private List<User> isRead = new ArrayList<>();
}