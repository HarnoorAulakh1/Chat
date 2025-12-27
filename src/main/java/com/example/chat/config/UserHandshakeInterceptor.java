package com.example.chat.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
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

        String query = request.getURI().getQuery();
//        if (query != null && query.contains("username=")) {
//            username = query.split("=").length>1? query.split("=")[1]:"";
//        }

        if(query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    attributes.put("token",param.substring(6));
                }
                else if(param.startsWith("username=")){
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
