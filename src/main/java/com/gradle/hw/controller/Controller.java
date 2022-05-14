package com.gradle.hw.controller;

import com.gradle.hw.model.User;
import com.gradle.hw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "Hello Gradle!";
    }

    @GetMapping("/users")
    public List<User> getUsers() {

        return userRepository.findAll();
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }
}
