package com.example.demo.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTOs.ChangeEmailRequest;
import com.example.demo.DTOs.ChangePasswordRequest;
import com.example.demo.database.entities.User;
import com.example.demo.database.repositories.UserRepository;
import com.example.demo.services.TokenService;

@RestController
@RequestMapping("/v1/auth")
public class ChangeInformation {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final TokenService tokenService = new TokenService();
    
    @Autowired
    private UserRepository userRepository;

    @PutMapping("/changeEmail")
    public ResponseEntity<Map<String, String>> changeEmail(@RequestHeader("Authorization") String token,
                                                          @Validated @RequestBody ChangeEmailRequest changeEmailRequest) {
        String accessToken = token.replace("Bearer ", "");
        Map<String, String> response = new HashMap<>();

        if (tokenService.verifyAccessToken(accessToken)) {
            String currentUserEmail = tokenService.getUserEmailFromToken(accessToken);
            Optional<User> existingUser = userRepository.findByEmail(currentUserEmail);

            if (existingUser.isPresent()) {
                Optional<User> userWithNewEmail = userRepository.findByEmail(changeEmailRequest.getNewEmail());
                if (userWithNewEmail.isPresent()) {
                    response.put("status", "error");
                    response.put("message", "The new email is already in use");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }

                if (passwordEncoder.matches(changeEmailRequest.getPassword(), existingUser.get().getPassword())) {
                    existingUser.get().setEmail(changeEmailRequest.getNewEmail());
                    userRepository.save(existingUser.get());

                    response.put("status", "success");
                    response.put("message", "Email changed successfully");
                    response.put("access_token", tokenService.generateAccessToken(existingUser));
                	response.put("refresh_token", tokenService.generateRefreshToken(existingUser));
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                } else {
                    response.put("status", "error");
                    response.put("message", "Incorrect password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            } else {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.put("status", "error");
            response.put("message", "Token is invalid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Map<String, String>> changePassword(@RequestHeader("Authorization") String token,
                                                              @Validated @RequestBody ChangePasswordRequest changePasswordRequest) {
        String accessToken = token.replace("Bearer ", "");
        Map<String, String> response = new HashMap<>();

        if (tokenService.verifyAccessToken(accessToken)) {
            String userEmail = tokenService.getUserEmailFromToken(accessToken);
            Optional<User> existingUser = userRepository.findByEmail(userEmail);

            if (existingUser.isPresent()) {
                if (passwordEncoder.matches(changePasswordRequest.getOldPassword(), existingUser.get().getPassword())) {
                    existingUser.get().setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                    userRepository.save(existingUser.get());

                    response.put("status", "success");
                    response.put("message", "Password changed successfully");
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                } else {
                    response.put("status", "error");
                    response.put("message", "Incorrect old password");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            } else {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.put("status", "error");
            response.put("message", "Token is invalid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
