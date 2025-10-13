package com.example.chat.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    @DBRef(lazy = true)
    private User sender;

    @DBRef(lazy = true)
    private User receiver;

    private String content;

    @DBRef(lazy = true)
    private Group group;

    private String image;

    private FileInfo file;

    private List<User> isRead = new ArrayList<>();
}