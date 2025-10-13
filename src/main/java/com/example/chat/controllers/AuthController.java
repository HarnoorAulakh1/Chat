package com.example.chat.controllers;


import com.example.chat.models.LoginRequest;
import com.example.chat.models.User;
import com.example.chat.repository.UserRepository;
import com.example.chat.utils.ImageService;
import com.example.chat.utils.JwtUtil;
import com.example.chat.utils.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ImageService imageService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserService userService, ImageService imageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userService=userService;
        this.imageService = imageService;
    }

    @PostMapping(value = "/register")
    public String register(
            @RequestPart("user") String userJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);

        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            return"Username already exists";
        }
        String imageUrl="";
        if(!file.isEmpty()) {
            try {
                imageUrl = imageService.uploadImage(file);
            } catch (Exception e) {
                System.out.println("file could not be saved");
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setProfilePicture(imageUrl);
        User userSaved=userService.save(user);
        userRepository.save(userSaved);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest user,
                                   HttpServletResponse response) {
        String username=user.getUsername();
        String password=user.getPassword();
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        }
        catch(Exception e){
            return new ResponseEntity<>("Username or Password Mismatch",HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtil.generateToken(username);

        // --- Set JWT in a cookie ---
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);           // Cannot be accessed via JavaScript (safer)
        cookie.setSecure(true);             // Only over HTTPS (set false for local dev)
        cookie.setPath("/");                // Cookie valid for all paths
        cookie.setMaxAge(60 * 60);          // 1 hour
        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful");
    }

    @GetMapping("/checkLogin")
    public ResponseEntity<?> checkLogin(HttpServletRequest req){
        String jwt = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JWT_TOKEN")) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        //System.out.println("token= "+ jwt);
        if (!jwt.isEmpty())  {
            if (jwtUtil.validateToken(jwt)) {
                Claims claims=jwtUtil.extractAllClaims(jwt);
                //System.out.println("token= "+ claims.get("username"));
                return new ResponseEntity<>(claims, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(ResponseMessage.builder()
                .message("Authentication failed")
                .build(),HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Loged Out");
    }

}
