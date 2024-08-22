package com.example.demo.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.TokenService;

@RestController
public class ValidateToken {

    private final TokenService tokenService = new TokenService();

    @GetMapping("/v1/auth/validate")
    public ResponseEntity<Map<String, String>> validateAccessToken(@RequestHeader("Authorization") String token) {
        String accessToken = token.replace("Bearer ", "");
        Map<String, String> response = new HashMap<>();

        if (tokenService.verifyAccessToken(accessToken)) {
            response.put("status", "success");
            response.put("message", "Token is valid");
            //
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        response.put("status", "error");
        response.put("message", "Token is invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
