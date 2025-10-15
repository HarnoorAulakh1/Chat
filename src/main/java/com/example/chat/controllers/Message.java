package com.example.chat.controllers;

import com.example.chat.service.MessageService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/message")
public class Message {

    @Autowired
    private MessageService messageService;

    @GetMapping("getMessages")
    public ResponseEntity<?> getMessages(@RequestParam String sender,@RequestParam String receiver){

        return new ResponseEntity<>(messageService.getMessages(sender,receiver), HttpStatus.OK);
    }
    @GetMapping("getUnreadCount")
    public ResponseEntity<?> getUnreadCount(@RequestParam String sender,@RequestParam String receiver,@RequestParam String readBy){

        return new ResponseEntity<>(messageService.getUnreadCount(sender,receiver,readBy), HttpStatus.OK);
    }
    @GetMapping("markAsRead")
    public ResponseEntity<?> markAsRead(@RequestParam String sender,@RequestParam String receiver,@RequestParam String readBy,@RequestParam String time){

        messageService.markAsRead(sender,receiver,time,readBy);
        return new ResponseEntity<>(ResponseMessage.builder().message("Marked read").build(), HttpStatus.OK);
    }
}
