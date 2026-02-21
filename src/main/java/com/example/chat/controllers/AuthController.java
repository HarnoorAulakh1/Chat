package com.example.chat.controllers;


import com.example.chat.models.LoginRequest;
import com.example.chat.models.User;
import com.example.chat.repository.UserRepository;
import com.example.chat.utils.ImageService;
import com.example.chat.utils.JwtUtil;
import com.example.chat.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ImageService imageService;
    private final MongoTemplate mongoTemplate;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserService userService, ImageService imageService, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userService=userService;
        this.imageService = imageService;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> register(
            @RequestPart("user") String userJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile file
    ) throws IOException {
        System.out.println("hi");
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(userJson, User.class);

        if(userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity("Username already exists",HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>(ResponseMessage.builder().message("Successfully registered").build(),HttpStatus.OK);
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

        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", token) .httpOnly(true)                  // not accessible from JS
                .secure(true)
                .sameSite("None")
                .domain("aulakh.site")
                .path("/")
                .maxAge(60 * 60)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

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

