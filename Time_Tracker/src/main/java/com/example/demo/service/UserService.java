package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticate(String id, String pass) {
    	Optional<User> userOptional = userRepository.findById(id);

    	if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getPass().equals(pass);
        }

        return false;
    }
    
    public boolean register(User user) {
        if (userRepository.existsById(user.getId())) {
            return false; // すでに同じIDのユーザーが存在する場合、登録を拒否
        }

        userRepository.save(user);
        return true;
    }
    
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

   
}