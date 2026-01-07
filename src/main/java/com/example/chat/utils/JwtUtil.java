package com.example.chat.utils;

import com.example.chat.models.Chatroom;
import com.example.chat.models.Member;
import com.example.chat.models.User;
import com.example.chat.repository.MemberRepository;
import com.example.chat.repository.RoomRepository;
import com.example.chat.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtil {

    @Autowired
    private UserService userService;

    private String SECRET_KEY = "TaK+HaV^uvCHEFsEVfypW#7g9^k*Z8$V";
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoomRepository roomRepository;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("username",String.class);
    }

    public String extractId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("id",String.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username",username);
        Optional<User> newUser=userService.findByUsername(username);
        if(newUser.isPresent()){
            claims.put("profilePicture",newUser.get().getProfilePicture());
            claims.put("id",newUser.get().getId());
            claims.put("name",newUser.get().getName());
            claims.put("role",newUser.get().getRole());
            claims.put("email",newUser.get().getEmail());
        }
        return createToken(claims, username);
    }
    public String generateTokenForChatroom(String roomId,String username,String memberId) {
        Map<String, Object> claims = new HashMap<>();
        Optional<Chatroom> chatroom=roomRepository.findById(roomId);
        claims.put("username",username);
        claims.put("roomId",roomId);
        if(chatroom.isPresent())
            claims.put("roomName",chatroom.get().getName());
        claims.put("memberId",memberId);
//        Optional<Member> newUser=memberRepository.findByUsername(username);
//        if(newUser.isPresent()){
//            claims.put("id",newUser.get().getId());
//        }
        return createTokenForChatroom(claims, username);
    }

    private String createTokenForChatroom(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}