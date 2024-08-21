package com.example.demo.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.database.entities.User;
import com.example.demo.database.repositories.UserRepository;

@RestController
public class SignupController {

    @Autowired
    UserRepository userRepository;

    public boolean checkEmailvalidity(String email) {
        // Regular expression to match standard email addresses
        String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(email_regex);
    }

    public static boolean strengthPasswordCheck(String password) {
        // Checking lower alphabet in string
        int passwordLength = password.length();
        boolean hasLower = false, hasUpper = false, hasDigit = false, specialChar = false;
        Set<Character> specialChars = new HashSet<>(Arrays.asList('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+'));
        
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (specialChars.contains(c)) specialChar = true;
        }

        return hasDigit && hasLower && hasUpper && specialChar && (passwordLength >= 8);
    }

    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<Map<String, String>> signupUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        
        if (checkEmailvalidity(user.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            if (existingUser.isPresent()) {
                response.put("status", "error");
                response.put("message", "Email is already in use");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else {
                if (strengthPasswordCheck(user.getPassword())) {
                    User newUser = new User(user.getEmail(), passwordEncoder.encode(user.getPassword()));
                    userRepository.save(newUser);
                    response.put("status", "success");
                    response.put("message", "User sign up successfully");
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                } else {
                    response.put("status", "error");
                    response.put("message", "Password is weak");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        } else {
            response.put("status", "error");
            response.put("message", "Invalid email address");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
