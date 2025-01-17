package com.example.demo.config.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

// JwtTokenProvider 클래스는 이메일을 기반으로 JWT 토큰을 생성
@Component
public class JwtTokenProvider {

    private final String secretKey = "mySecretKey"; // 비밀 키 (환경 변수나 별도 파일에 저장해야 합니다.)
    private final long validityInMilliseconds = 3600000; // 1시간

    // JWT 토큰 생성
    public String generateToken(String email) {
        Claims claims = Jwts.claims().setSubject(email); // 사용자 이메일을 Subject로 설정
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds); // 유효기간 설정

        return Jwts.builder()
                .setClaims(claims) // 클래임
                .setIssuedAt(now) // 발급 시간
                .setExpiration(validity) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘, 비밀 키
                .compact();

        // 최종적으로 JWT 토큰을 생성
        // String jwtToken = builder.compact();
    }

    // JWT 토큰에서 사용자 이메일 추출
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); // 토큰 검증
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
