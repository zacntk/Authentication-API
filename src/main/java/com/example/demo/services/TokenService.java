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
    static Dotenv dotenv = Dotenv.load();

    private static final String ACCESS_TOKEN_SECRET = dotenv.get("ACCESS_TOKEN_SECRET");
    private static final String REFRESH_TOKEN_SECRET = dotenv.get("REFRESH_TOKEN_SECRET");
    private static final String ISSUER = dotenv.get("APP_NAME");
    private static final String AUDIENCE = dotenv.get("APP_AUDIENCE");

    public String generateAccessToken(Optional<User> existingUser) {
        User user = existingUser.orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", user.getId());
        payload.put("roles", user.getRole());
        payload.put("email", user.getEmail());

        Date expirationTime = getExpirationDate(10 * 60); // 10 minutes

        return JWT.create()
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withSubject(String.valueOf(user.getId()))
                .withPayload(payload)
                .withExpiresAt(expirationTime)
                .sign(Algorithm.HMAC256(ACCESS_TOKEN_SECRET));
    }

    public String generateRefreshToken(Optional<User> existingUser) {
        User user = existingUser.orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", user.getId());
        payload.put("roles", user.getRole());
        payload.put("email", user.getEmail());

        Date expirationTime = getExpirationDate(24 * 3600); // 24 hours

        return JWT.create()
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withSubject(String.valueOf(user.getId()))
                .withPayload(payload)
                .withExpiresAt(expirationTime)
                .sign(Algorithm.HMAC256(REFRESH_TOKEN_SECRET));
    }

    public boolean verifyAccessToken(String token) {
        return verifyToken(token, ACCESS_TOKEN_SECRET);
    }

    public boolean verifyRefreshToken(String token) {
        return verifyToken(token, REFRESH_TOKEN_SECRET);
    }

    public String getUserEmailFromToken(String token) {
        try {
            if (verifyAccessToken(token)) {
                DecodedJWT decodedJWT = JWT.decode(token);
                return decodedJWT.getClaim("email").asString();
            }
            return null; // token ไม่ถูกต้อง
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean verifyToken(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Date getExpirationDate(int seconds) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }
}
