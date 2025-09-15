package com.financiasheet.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;      // <-- importante
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;     // <-- SecretKey, não Key
    private final long expMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes}") long expMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());   // retorna SecretKey
        this.expMs = expMinutes * 60_000;
    }

    public String generateToken(String subject) {
        var now = new Date();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expMs))
                .signWith(key)               // HS256 com SecretKey
                .compact();
    }

    public String extractSubject(String token) {
        return Jwts.parser()
                .verifyWith(key)             // aceita SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
