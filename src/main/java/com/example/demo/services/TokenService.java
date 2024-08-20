package com.example.demo.services;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.demo.database.entities.User;

import io.github.cdimascio.dotenv.Dotenv;

public class TokenService {
	static Dotenv dotenv = Dotenv.load(); // Load .env file

	private static final String ACCESS_TOKEN_SECRET = dotenv.get("ACCESS_TOKEN_SECRET");
	private static final String REFRESH_TOKEN_SECRET = dotenv.get("REFRESH_TOKEN_SECRET");

	public String generateAccessToken(Optional<User> existingUser) {
		Map<String, Object> header = new HashMap<>();
		header.put("alg", "HS256");
		header.put("typ", "JWT");

		Map<String, Object> payload = new HashMap<>();
		payload.put("Id", existingUser.get().getId());
		payload.put("email", existingUser.get().getEmail());

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.SECOND, 60*10);
		Date expirationTime = calendar.getTime();

		return JWT.create().withHeader(header).withPayload(payload).withExpiresAt(expirationTime)
				.sign(Algorithm.HMAC256(ACCESS_TOKEN_SECRET));
	}

	public String generateRefreshToken(Optional<User> existingUser) {
		Map<String, Object> header = new HashMap<>();
		header.put("alg", "HS256");
		header.put("typ", "JWT");

		Map<String, Object> payload = new HashMap<>();
		payload.put("Id", existingUser.get().getId());
		payload.put("email", existingUser.get().getEmail());

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.SECOND, 3600 * 24);
		Date expirationTime = calendar.getTime();

		return JWT.create().withHeader(header).withPayload(payload).withExpiresAt(expirationTime)
				.sign(Algorithm.HMAC256(REFRESH_TOKEN_SECRET));
	}

	public boolean verifyAccessToken(String token) {
	    try {
	        Algorithm algorithm = Algorithm.HMAC256(ACCESS_TOKEN_SECRET);
	        JWTVerifier verifier = JWT.require(algorithm).build();
	        verifier.verify(token);
	        return true; // Token is valid
	    } catch (Exception e) {
	        return false; // Token is invalid
	    }
	}

	public boolean verifyRefreshToken(String token) {
	    try {
	        Algorithm algorithm = Algorithm.HMAC256(REFRESH_TOKEN_SECRET);
	        JWTVerifier verifier = JWT.require(algorithm).build();
	        verifier.verify(token);
	        return true; // Token is valid
	    } catch (Exception e) {
	        return false; // Token is invalid
	    }
	}


	public Long getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("Id").asLong(); // Extract user ID from token
        } catch (Exception e) {
            // Handle token parsing errors
            e.printStackTrace();
            return null;
        }
    }

}
