package com.example.chat.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "group")
@Data
public class Group {

    @Id
    private String id;
    private String name;
    @DBRef
    private List<User> admins=new ArrayList<>();
    private String logo;
    @DBRef
    private List<User> members=new ArrayList<>();
    private List<Saved> saved=new ArrayList<>();
}


class Saved{
    private String type;
    private String name;
    private String link;
}