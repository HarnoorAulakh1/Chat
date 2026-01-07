package com.example.chat.service;

import com.example.chat.models.*;
import com.example.chat.repository.MemberRepository;
import com.example.chat.repository.RoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RedisSubscriberChat {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MemberRepository memberRepository;

    public void onMessage(String message, String channel) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
        RedisMessage<Message> json1=mapper.readValue(message, new TypeReference<RedisMessage<Message>>() {});
        Message json= json1.getPayload();
        String roomId=json.getRoomId();
        String destination=json1.getDestination();
        //System.out.println("Json= "+json);
        if (json.getSender() != null) {
            if(roomId==null) {
                Optional<User> user1 = userService.findById(json.getSender());
                if (user1.isPresent())
                    json.setSenderEm(user1.get());
            }
            else{
                Optional<Member> member=memberRepository.findById(json.getSender());
                if (member.isPresent())
                    json.setSenderEm(User.builder().username(member.get().getUsername()).id(member.get().getId()).email("").password("").name("").build());
            }
        }
        if (json.getReceiver() != null) {
            Optional<User> user1=userService.findById(json.getReceiver());
            if(user1.isPresent())
                json.setReceiverEm(user1.get());
        }
        if(roomId==null) {
            simpMessagingTemplate.convertAndSendToUser(json.getReceiver(), destination, json);
        }
        else{
            Optional<Chatroom> room=roomRepository.findById(roomId);
            if(room.isPresent()){
                List<String> members=room.get().getMembers();
                for(String id:members){
                    if(!id.equals(json.getSender())) {
                       // System.out.println("member= "+id);
                        simpMessagingTemplate.convertAndSendToUser(id, destination, json);
                    }
                }
            }
        }
        if(destination.equals("/topic/messages")) {
            //System.out.println("Message sent= "+json.getContent());
            simpMessagingTemplate.convertAndSendToUser(json.getSender(), "/topic/preview", json);
            simpMessagingTemplate.convertAndSendToUser(json.getReceiver(), "/topic/preview", json);
        }
    }
}
