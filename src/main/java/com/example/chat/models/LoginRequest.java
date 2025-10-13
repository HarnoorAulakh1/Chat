package com.example.chat.models;

import lombok.Data;

@Data
public class LoginRequest{
    private String username;
    private String password;
}