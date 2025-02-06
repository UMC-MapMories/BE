package com.example.demo.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

// jwt 생성, 검증
@Component
public class JWTUtil {

    private SecretKey secretKey;

    // String secret key 기반 객체 키를 생성
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }

    // 토큰 검증하는 세개의 메서드
    public String getEmail(String token) {
        // String type username 가져오기
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    /*
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
    */


    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public LocalDateTime getExpiration(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expirationDate = claims.getExpiration();
        return LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
    }

    // 토큰 생성 메서드, 인자를 받아서 토큰을 응답
    public String createJwt(Long id, String email, Long expiredMs) {

        return Jwts.builder()
                .claim("id", id)
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}