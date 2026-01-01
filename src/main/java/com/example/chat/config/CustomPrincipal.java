package com.example.chat.config;

import io.jsonwebtoken.Claims;
import lombok.Getter;

import java.security.Principal;

public class CustomPrincipal implements Principal {
    private final String userId;
    @Getter
    private final Claims claims;

    public CustomPrincipal(String userId, Claims claims) {
        this.userId = userId;
        this.claims = claims;
    }

    @Override
    public String getName() {
        return userId;
    }

}