package com.example.chat.config;

import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.http.server.ServerHttpRequest;

import java.security.Principal;
import java.util.Map;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String username = (String) attributes.get("username");
        if (username == null) username = "unknown";
        System.out.println("username in handshake ="+username);
        String finalUsername = username;
        return () -> finalUsername;
    }
}
