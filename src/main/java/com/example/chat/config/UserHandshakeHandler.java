package com.example.chat.config;

import com.example.chat.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.http.server.ServerHttpRequest;

import java.security.Principal;
import java.util.Map;


@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    public UserHandshakeHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String token = (String) attributes.get("JWT_TOKEN");
        String userId= jwtUtil.extractId(token);
        //System.out.println("HandshakeHandler= "+userId);

        if (userId == null) userId = "unknown";
        String finalUserId = userId;
        return () -> finalUserId;
    }
}
