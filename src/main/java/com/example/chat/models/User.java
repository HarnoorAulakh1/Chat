package com.example.chat.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection="user")
public class User {

    @Id
    private String id;
    private String role;
    @Indexed(unique = true)
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String name;
    @NonNull
    private String email;
    private String profilePicture;
    @DBRef
    private List<User> friends=new ArrayList<>();
    @DBRef
    private List<Group> groups=new ArrayList<>();
    private boolean isOnline;

}
