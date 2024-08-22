package com.example.demo.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.database.entities.User;
import com.example.demo.database.repositories.UserRepository;
import com.example.demo.services.TokenService;

@RestController
@RequestMapping("/v1/auth")
public class RefreshToken {

    private final TokenService tokenService = new TokenService();

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String token) {
        String refreshToken = token.replace("Bearer ", "");
        Map<String, String> response = new HashMap<>();

        if (tokenService.verifyRefreshToken(refreshToken)) {
            String userEmail = tokenService.getUserEmailFromToken(refreshToken);
            Optional<User> existingUser = userRepository.findByEmail(userEmail);

            if (existingUser.isPresent()) {
                response.put("access_token", tokenService.generateAccessToken(existingUser));
                response.put("refresh_token", tokenService.generateRefreshToken(existingUser));
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        }
        response.put("status", "error");
        response.put("message", "Token is invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
