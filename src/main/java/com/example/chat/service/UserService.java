package com.example.chat.service;
import com.example.chat.models.User;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository ob1;

    public User save(User user) {
        return ob1.save(user);
    }
    public List<User> findAll(){
        return ob1.findAll();
    }

    public Optional<User> findById(String id){
        return ob1.findById(id);
    }

    public List<User> getFriends(String id){
        Optional<User> user=ob1.findById(id);
        if(user.isPresent())
            return user.get().getFriends();
        return new ArrayList<User>();
    }

    public Optional<User> findByUsername( String username){
        return ob1.findByUsername(username);
    }
    public Optional<User> findByEmail(String email){
        return ob1.findByEmail(email);
    }
}
