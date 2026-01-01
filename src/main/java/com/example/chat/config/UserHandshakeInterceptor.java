package com.example.chat.config;

import com.example.chat.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

@Component
public class UserHandshakeInterceptor implements HandshakeInterceptor {


    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,

                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        // Get username from query param or header
        String username = "unknown";
        HttpServletRequest req;
        String query = request.getURI().getQuery();
        if (request instanceof ServletServerHttpRequest servletRequest) {
            req = servletRequest.getServletRequest();
            Cookie[] cookies = req.getCookies();
           // System.out.println(cookies);
            if(cookies!=null){
            for (Cookie cookie : cookies)
                if(cookie.getName().equals("JWT_TOKEN")) {
                    attributes.put("JWT_TOKEN", cookie.getValue());
                }
            }
        }

        if(query != null) {
            for (String param : query.split("&")) {
                if(param.startsWith("username=")){
                    attributes.put("username",param.substring(9));
                }
            }
        }

        // Here we return a Principal via attributes, but to make it work with STOMP:
        //attributes.put("username", username);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               @Nullable Exception exception) {}
}
