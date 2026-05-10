package org.example.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${JWT_SECRET}")
    private String secret;

    @Value("${JWT_EXPIRATION}")
    private int expirationSeconds;

    public String generateToken(String username, String role){
        return Jwts.builder()
                .subject(username)
                .claim("role",role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationSeconds*1000L))
                .signWith(signKey())
                .compact();
    }

    public boolean validate(String token){
        try {
            Jwts.parser()
                    .verifyWith(signKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public String getUsername(String token){
        return getClaims(token).getSubject();
    }

    public String getRole(String token){
        return getClaims(token).get("role",String.class);
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
