package com.example.chat.controllers;

import com.example.chat.kafka.KafkaProducer;
import com.example.chat.models.FileInfo;
import com.example.chat.models.Message;
import com.example.chat.service.MessageService;
import com.example.chat.utils.ImageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private ImageService imageService;

    @GetMapping("/getMessages")
    public ResponseEntity<?> getMessages(@RequestParam String sender,@RequestParam String receiver,@RequestParam String roomId){
        //System.out.println(sender+" "+roomId);
        return new ResponseEntity<>(messageService.getMessages(sender,receiver,roomId), HttpStatus.OK);
    }
    @GetMapping("getUnreadCount")
    public ResponseEntity<?> getUnreadCount(@RequestParam String sender,@RequestParam String receiver,@RequestParam String readBy){

        return new ResponseEntity<>(messageService.getUnreadCount(sender,receiver,readBy), HttpStatus.OK);
    }
    @PostMapping("/markAsRead")
    public ResponseEntity<?> markAsRead(@RequestParam String sender,@RequestParam String receiver,@RequestParam String readBy,@RequestParam String time) throws JsonProcessingException {
        messageService.markAsRead(sender,receiver,time,readBy);
        return new ResponseEntity<>(ResponseMessage.builder().message("Marked read").build(), HttpStatus.OK);
    }

    @PostMapping("/send")
    public void message(Principal principal,
                         @RequestPart("message") String message,
                         @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        String id=principal.getName();
        //System.out.println(message);
        Message json=objectMapper.readValue(message,Message.class);
        json.setId(UUID.randomUUID().toString());
        // calll message service push ,
        String imageUrl="";
        if(file!=null && !file.isEmpty()) {
            try {
                imageUrl = imageService.uploadImage(file);
            } catch (Exception e) {
                System.out.println("file could not be saved");
            }
        }
        FileInfo fileInfo=json.getFile();
        fileInfo.setLink(imageUrl);
        json.setFile(fileInfo);
        String key=json.getReceiver().compareTo(json.getSender())>0?json.getReceiver()+json.getSender():json.getSender()+json.getReceiver();
        // messageService.push(message);
        kafkaProducer.sendMessage("chat-database",key,json);
    }

}
