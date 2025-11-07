package com.example.chat.controllers;

import com.example.chat.models.Notifications;
import com.example.chat.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class Notification {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/getNotifications/{id}")
    public ResponseEntity<?> getNotifications(@PathVariable String id){
        if(id==null || id.isEmpty())
            return new ResponseEntity<>(ResponseMessage.builder().message("Params not found"), HttpStatus.BAD_REQUEST);
        List<Notifications> notifications=notificationRepository.findByReceiver(id);
        return new ResponseEntity<>(notifications,HttpStatus.OK);
    }

    @GetMapping("/deleteNotification/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id){
        if(id==null || id.isEmpty()) {
            return new ResponseEntity<>(ResponseMessage.builder().message("Params not found").build(), HttpStatus.BAD_REQUEST);
        }
        notificationRepository.deleteById(id);
        return new ResponseEntity<>(ResponseMessage.builder().message("Notification deleted successfully").build(),HttpStatus.OK);
    }
}
