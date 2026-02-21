package com.example.chat.config;

import com.example.chat.models.User;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.HandshakeHandler;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker  // enables STOMP over WebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    private final JwtChannelInterceptor jwtChannelInterceptor;
    private final UserHandshakeHandler userHandshakeHandler;

    public WebSocketConfig(JwtChannelInterceptor jwtChannelInterceptor,UserHandshakeHandler userHandshakeHandler) {
        this.jwtChannelInterceptor = jwtChannelInterceptor;
        this.userHandshakeHandler=userHandshakeHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Client can subscribe to destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic", "/queue");

        // Messages bound for @MessageMapping are prefixed with "/app"
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new UserHandshakeInterceptor())
                .setAllowedOriginPatterns("http://localhost:5173","https://video.aulakh.site")
                .withSockJS();
    }


//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        Principal principal = accessor.getUser();
//
//        if (principal != null) {
//            String username = principal.getName();
//            String sessionId = accessor.getSessionId();
//            //System.out.println("User connected: " + username + " -> " + sessionId);
//        } else {
//            System.out.println("No principal found for session: " + accessor.getSessionId());
//        }
//    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }



}
