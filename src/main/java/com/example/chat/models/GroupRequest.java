package com.example.chat.models;

import lombok.Data;

@Data
public class GroupRequest{
    // this name serves different purposes in the above controllers
    private String name;
    private String id;
}