package com.example.chat.controllers;
import com.example.chat.models.Message;
import com.example.chat.models.Notifications;
import com.example.chat.models.User;
import com.example.chat.repository.NotificationRepository;
import com.example.chat.service.MessageService;
import com.example.chat.service.NotificationService;
import com.example.chat.service.RedisPublisher;
import com.example.chat.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Controller
public class Socket {

    @Autowired
    private SimpMessagingTemplate template;


    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;


    @MessageMapping("/send")
    public void send(Message message) throws JsonProcessingException {
        messageService.push(message);
    }

    @MessageMapping("/FriendReq")
    public void friend_req(Notifications message) throws JsonProcessingException {
        Optional<User> user1=userService.findById(message.getSender());
        if(user1.isPresent())
            notificationService.push(message.getSender(), message.getReceiver(), "Friend request from "+user1.get().getUsername(),"friend_req");
    }

    @MessageMapping("/FriendReqAction")
    public void friend_req_action(Notifications message) throws JsonProcessingException {
        if(message.getDescription()==null)
            return;
        String action=message.getDescription();
        Optional<User> user1=userService.findById(message.getSender());
        notificationRepository.deleteById(message.getId());
        System.out.println(message.getDescription()+" "+message.getId());
        if(user1.isPresent()) {
            if (action.equals("accepted")) {
                userService.addFriend(message.getSender(), message.getReceiver());
                notificationService.push(message.getSender(), message.getReceiver(), user1.get().getUsername() + " accepted you friend request", "friend_req");
            } else if (action.equals("rejected")) {
                notificationService.push(message.getSender(), message.getReceiver(), user1.get().getUsername() + " rejected you friend request", "friend_req");
            }
        }
    }

    @MessageMapping("/connect")
    public void check(ConnectDto message) {
        System.out.println("socket connected");
    }

}

@Data
class ConnectDto{
    private String username;
}
