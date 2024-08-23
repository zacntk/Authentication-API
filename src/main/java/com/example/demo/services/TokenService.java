package com.example.demo.services;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.demo.database.entities.User;

import io.github.cdimascio.dotenv.Dotenv;

public class TokenService {
	static Dotenv dotenv = Dotenv.load();

	private static final String ACCESS_TOKEN_SECRET = dotenv.get("ACCESS_TOKEN_SECRET");
	private static final String REFRESH_TOKEN_SECRET = dotenv.get("REFRESH_TOKEN_SECRET");
	private static final String ISSUER = dotenv.get("ISSUER");
	private static final String AUDIENCE = dotenv.get("AUDIENCE");

	public String generateAccessToken(Optional<User> existingUser) {
		return generateToken(existingUser, ACCESS_TOKEN_SECRET, 10 * 60); // 10 minutes
	}

	public String generateRefreshToken(Optional<User> existingUser) {
		return generateToken(existingUser, REFRESH_TOKEN_SECRET, 24 * 3600); // 24 hours
	}

	private String generateToken(Optional<User> existingUser, String secret, int expirationSeconds) {
		User user = existingUser.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Date now = new Date();
		Date expirationTime = new Date(now.getTime() + expirationSeconds * 1000L);

		return JWT.create().withIssuer(ISSUER).withAudience(AUDIENCE).withSubject(String.valueOf(user.getId()))
				.withClaim("role", user.getRole()).withIssuedAt(now).withExpiresAt(expirationTime)
				.sign(Algorithm.HMAC256(secret));
	}

	public boolean verifyAccessToken(String token) {
		return verifyToken(token, ACCESS_TOKEN_SECRET);
	}

	public boolean verifyRefreshToken(String token) {
		return verifyToken(token, REFRESH_TOKEN_SECRET);
	}

	public Long getUserIdFromToken(String token) {
		try {
			DecodedJWT decodedJWT = JWT.decode(token);
			return decodedJWT.getSubject() != null ? Long.parseLong(decodedJWT.getSubject()) : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean verifyToken(String token, String secret) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).withAudience(AUDIENCE).build();
			verifier.verify(token);
			return true;
		} catch (Exception e) {
			System.err.println("Token verification failed: " + e.getMessage());
			return false;
		}
	}
}
