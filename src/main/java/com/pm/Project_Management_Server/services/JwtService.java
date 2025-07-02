package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.entity.User;
import com.pm.Project_Management_Server.repositories.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtService {
    private static final long EXPIRATION_TIME = 86400000; // 1 day in ms
    private static final String SECRET_KEY = "4c838ffa9a414923bcb00e7a8cf44a0d4c838ffa9a414923bcb00e7a8cf44a0d"; // should be 32-byte key
    private final UserRepository userRepo;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Jwts.builder()
                .setSubject(email)
                .claim("id", user.getId())
                .claim("role", user.getUserType().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
