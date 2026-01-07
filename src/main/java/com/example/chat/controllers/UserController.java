package com.example.chat.controllers;

import com.example.chat.models.Message;
import com.example.chat.models.User;
import com.example.chat.service.MessageService;
import com.example.chat.utils.JwtUtil;
import com.example.chat.service.UserService;
import lombok.Builder;
import lombok.Data;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.NullValue;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private  MongoTemplate mongoTemplate;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @GetMapping("/isOnline/{id}")
    public ResponseEntity<?> isOnline(@PathVariable String id){
        if(id.isEmpty())
            return new ResponseEntity<>( ResponseMessage.builder()
                    .message("Id not present")
                    .build()
                    ,HttpStatus.BAD_REQUEST);

        Optional<User> user=userService.findById(id);
        //System.out.println("Users Connected= "+simpUserRegistry.getUsers());
        if(!user.isPresent())
            return new ResponseEntity<>( ResponseMessage.builder().message("User not present").build(), HttpStatus.BAD_REQUEST);
        boolean isOnline;
        try {
            isOnline=simpUserRegistry.getUser(id).getSessions().size()!=0;
        }
        catch (Exception e){
            isOnline=false;
        }
        return new ResponseEntity<>(ResponseMessage.builder().message("Successfully extracted user").isOnline(isOnline).build(),HttpStatus.OK);
    }

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        if (id == null || id.isEmpty())
            return new ResponseEntity<>(ResponseMessage.builder().message("Username is required"), HttpStatus.NO_CONTENT);

        Optional<User> user=userService.findById(id);
        if(!user.isPresent())
            return new ResponseEntity<>(ResponseMessage.builder().message("No user found").build(),HttpStatus.OK);
        return new ResponseEntity<>(user.get(),HttpStatus.OK);
    }
    @GetMapping("/getUsers/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id){
        if(id==null || id.isEmpty())
            return new ResponseEntity<>(ResponseMessage.builder().message("Username is required"),HttpStatus.NO_CONTENT);
        Query query=new Query();
        query.addCriteria(Criteria.where("username").regex(id));
        List<User> users=mongoTemplate.find(query,User.class);

        return new ResponseEntity<>(users,HttpStatus.OK);
    }
    @GetMapping("/getPreview")
    public ResponseEntity<?> getPreview(@RequestParam String sender,@RequestParam String receiver, @RequestParam(required = false) String group){
        //if(group.isEmpty()){
        if(sender==null || receiver==null || sender.isEmpty() || receiver.isEmpty())
            return new ResponseEntity<>(ResponseMessage.builder().message("sender or receiver missing").build(),HttpStatus.NO_CONTENT);
        Message preview=messageService.getPreview(sender,receiver);
        return new ResponseEntity<>(preview,HttpStatus.OK);
    }

    @GetMapping("/getFriends/{id}")
    public ResponseEntity<?> getFriends(@PathVariable String id){
        if(id==null || id.isEmpty())
            return new ResponseEntity<>(ResponseMessage
                    .builder()
                    .message("User ID is required")
                    .build(),HttpStatus.BAD_REQUEST);
        List<User> friends=userService.getFriends(id);
        return new ResponseEntity<>(friends,HttpStatus.OK);
    }
}


@Data
@Builder
class ResponseMessage{
    private String message;
    private Boolean isOnline;
}
