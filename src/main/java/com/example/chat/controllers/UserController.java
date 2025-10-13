package com.example.chat.controllers;

import com.example.chat.models.User;
import com.example.chat.utils.JwtUtil;
import com.example.chat.utils.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/isOnline/{id}")
    public ResponseEntity<?> isOnline(@PathVariable String id){
        if(id.isEmpty())
            return new ResponseEntity<>( ResponseMessage.builder()
                    .message("Id not present")
                    .build()
                    ,HttpStatus.BAD_REQUEST);

        Optional<User> user=userService.findById(id);
        if(!user.isPresent())
            return new ResponseEntity<>( ResponseMessage.builder().message("User not present").build(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(ResponseMessage.builder().message("Successfully extracted user").isOnline(user.get().isOnline()).build(),HttpStatus.OK);
    }


}


@Data
@Builder
class ResponseMessage{
    private String message;
    private Boolean isOnline;
}