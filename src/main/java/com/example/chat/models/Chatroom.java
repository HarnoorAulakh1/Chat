package com.example.chat.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "room")
@Data
@Builder
public class Chatroom {

    @Id
    private String id;
    private String name;
    private List<String> members=new ArrayList<>();
    private String logo;
    private List<Saved> saved=new ArrayList<>();
    @Builder.Default
    @Indexed(expireAfter = "3600s")
    private Date createdAt=new Date();
}


class Saved{
    private String type;
    private String name;
    private String link;
}