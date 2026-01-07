package com.example.chat.config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;


@Component
public class JwtChannelInterceptor implements ChannelInterceptor {


    private final String secret;

    public JwtChannelInterceptor(@Value("${jwt.secret}") String secret){
        this.secret=secret;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        // 🔐 Authenticate only on CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes == null) {
                throw new IllegalArgumentException("No session attributes");
            }

            String token = (String) sessionAttributes.get("JWT_TOKEN");

            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Missing JWT token");
            }

            try {
                SecretKey key = Keys.hmacShaKeyFor(
                        secret.getBytes(StandardCharsets.UTF_8)
                );
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();
                String id="",roomId=claims.get("roomId",String.class);
                if(roomId!=null)
                    id=claims.get("memberId",String.class);
                else
                    id=claims.get("id",String.class);

//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        claims,
//                        null,
//                        List.of()
//                );

//                System.out.println("check 1"+token);
//                System.out.println("Id= "+id);

                CustomPrincipal customPrincipal=new CustomPrincipal(id,claims);
                accessor.setUser(customPrincipal);
            } catch (JwtException ex) {
                // Invalid / expired token → reject CONNECT
                throw new IllegalArgumentException("Invalid JWT token");
            }
        }
        return message;
    }
}
