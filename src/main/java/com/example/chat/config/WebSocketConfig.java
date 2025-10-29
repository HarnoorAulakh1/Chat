package com.example.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker  // enables STOMP over WebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final UserHandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(UserHandshakeInterceptor handshakeInterceptor) {
        this.handshakeInterceptor = handshakeInterceptor;
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
                .setHandshakeHandler(new UserHandshakeHandler())
                .addInterceptors(new UserHandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();

        if (principal != null) {
            String username = principal.getName();
            String sessionId = accessor.getSessionId();
            //System.out.println("User connected: " + username + " -> " + sessionId);
        } else {
            System.out.println("No principal found for session: " + accessor.getSessionId());
        }
    }

}
