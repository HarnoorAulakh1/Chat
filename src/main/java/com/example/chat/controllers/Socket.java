package com.example.chat.controllers;
import com.example.chat.models.Ice;
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
import java.util.List;
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
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


//    @MessageMapping("/send")
//    public void send(Message message) throws JsonProcessingException {
//        messageService.push(message);
//    }



    @MessageMapping("/FriendReq")
    public void friend_req(Notifications message) throws JsonProcessingException {
        Optional<User> user1=userService.findById(message.getSender());
        if(user1.isPresent()) {
            List<String> friends=user1.get().getFriends();
            if(friends.contains(message.getReceiver()))
                notificationService.push(message.getSender(), message.getReceiver(), "Already friend , cant send again ", "info", "/topic/notifications");
            else
                notificationService.push(message.getSender(), message.getReceiver(), "Friend request from " + user1.get().getUsername(), "friend_req", "/topic/notifications");
        }
    }

    @MessageMapping("/FriendReqAction")
    public void friend_req_action(Notifications message) throws JsonProcessingException {
        //System.out.println(message);
        if(message.getDescription()==null)
            return;
        String action=message.getDescription();
        Optional<User> user1=userService.findById(message.getSender());
        notificationRepository.deleteById(message.getId());
        if(user1.isPresent()) {
            if (action.equals("accepted")) {
                userService.addFriend(message.getSender(), message.getReceiver());
                notificationService.push(message.getReceiver(), message.getSender(), "Adding"+user1.get().getUsername()+" to your friends", "friend_req_accepted","/topic/friendAccepted");
                notificationService.push(message.getSender(), message.getReceiver(), user1.get().getUsername() + " accepted you friend request", "friend_req_accepted","/topic/friendAccepted");
                notificationService.push(message.getSender(), message.getReceiver(), user1.get().getUsername() + " accepted you friend request", "friend_req_accepted","/topic/notifications");
            }
        }
    }

    @MessageMapping("/connect")
    public void check(ConnectDto message) {
        System.out.println("socket connected");
    }

    @MessageMapping("/typing")
    public void typing(Notifications message) throws JsonProcessingException {
        notificationService.push(message.getSender(), message.getReceiver(), message.getDescription(),"info","/topic/typing");
    }

}

@Data
class ConnectDto{
    private String username;
}
