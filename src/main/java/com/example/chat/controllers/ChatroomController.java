package com.example.chat.controllers;
import com.example.chat.models.Chatroom;
import com.example.chat.models.GroupRequest;
import com.example.chat.models.Member;
import com.example.chat.repository.MemberRepository;
import com.example.chat.repository.RoomRepository;
import com.example.chat.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.apache.catalina.connector.Response;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.mongodb.core.query.Query;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/room")
public class ChatroomController {

    private final RoomRepository roomRepository;
    private final JwtUtil jwtUtil;
    private final MongoTemplate mongoTemplate;
    private final MemberRepository memberRepository;

    public ChatroomController(RoomRepository roomRepository, JwtUtil jwtUtil, MemberRepository memberRepository,MongoTemplate mongoTemplate){
        this.roomRepository=roomRepository;
        this.jwtUtil=jwtUtil;
        this.memberRepository = memberRepository;
        this.mongoTemplate=mongoTemplate;
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody GroupRequest data, HttpServletResponse response, Principal principal){
        String roomId= data.getId();
        String username=data.getName();
        Optional<Chatroom> chatroom=roomRepository.findById(roomId);
        if(!chatroom.isPresent())
            return new ResponseEntity<>(ResponseMessage.builder().message("Invalid Room Id").build(), HttpStatus.BAD_REQUEST);
        try {
            String memberId=null;
            //System.out.println("principal= "+principal);
            if(principal!=null) {
                memberId = principal.getName();
                //System.out.println("memberId= "+memberId);
            }
            if(memberId==null)
                memberId=String.valueOf(UUID.randomUUID());
            Member member=Member.builder().username(username).id(memberId).build();
            memberRepository.save(member);

            Query query = new Query(Criteria.where("_id").is(roomId));
            Update update = new Update().addToSet("members", memberId);
            mongoTemplate.updateFirst(query, update, Chatroom.class);

            String token = jwtUtil.generateTokenForChatroom(roomId, username,memberId);
            Cookie cookie = new Cookie("JWT_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);
            response.addCookie(cookie);
            return new ResponseEntity<>(ResponseMessage.builder().message("Room joined successfully").build(), HttpStatus.OK);
        }
        catch(Exception err){
            System.out.println(err.getMessage());
            return new ResponseEntity<>(ResponseMessage.builder().message("Failed to Join the room").build(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody GroupRequest data,HttpServletRequest response){
        String name=data.getName();
        String id= String.valueOf(UUID.randomUUID());
        try {
            Chatroom chatroom = Chatroom.builder().name(name).id(id).build();
            roomRepository.save(chatroom);
            return new ResponseEntity<>(ResponseMessage.builder().message(id).build(), HttpStatus.OK);
        }
        catch(Exception err){
            return new ResponseEntity<>(ResponseMessage.builder().message(err.getMessage()).build(),HttpStatus.BAD_REQUEST);
        }
    }
}

