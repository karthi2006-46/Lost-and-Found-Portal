package com.example.lostfound.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtTokenUtil {
    private final Key key;
    private final long expirationMs;

    public JwtTokenUtil(@Value("${jwt.secret}") String secret,
                        @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public String getRole(String token) {
        Object r = parseClaims(token).getBody().get("role");
        return r == null ? null : r.toString();
    }
}