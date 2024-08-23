package com.example.demo.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.database.entities.User;
import com.example.demo.database.repositories.UserRepository;
import com.example.demo.services.TokenService;

@RestController
@RequestMapping("/v1/auth")
public class DeleteUser {

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final TokenService tokenService = new TokenService();

	@Autowired
	private UserRepository userRepository;

	@DeleteMapping("/deleteUser")
	public ResponseEntity<Map<String, String>> deleteUser(
	        @RequestHeader("Authorization") String token,
	        @Validated @RequestBody User deleteUser) {

	    String accessToken = token.replace("Bearer ", "");
	    Map<String, String> response = new HashMap<>();

	    if (tokenService.verifyAccessToken(accessToken)) {
	    	Long currentUserId = tokenService.getUserIdFromToken(accessToken);
            Optional<User> existingUser = userRepository.findById(currentUserId);

	        if (existingUser.isPresent()) {
	            User user = existingUser.get();
	            if (user.getEmail().equals(deleteUser.getEmail()) && passwordEncoder.matches(deleteUser.getPassword(), user.getPassword())) {
	                userRepository.delete(user);
	                response.put("status", "success");
	                response.put("message", "User deleted successfully");
	                return ResponseEntity.ok(response);
	            } else {
	                response.put("status", "error");
	                response.put("message", "Incorrect email or password");
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	            }
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
