package com.example.demo.controller;

import com.example.demo.jwt.JWTUtil;
import com.example.demo.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
// 로그아웃
public class LogoutController {

    private final JWTUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public LogoutController(JWTUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/customLogout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorization ) {

        try {
            System.out.println("1");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                System.out.println("2");

                String token = authorization.split(" ")[1];

                System.out.println("logout" + token);

                // JWT 토큰의 만료 시간 확인
                LocalDateTime expiresAt = jwtUtil.getExpiration(token);

                System.out.println("logout " + expiresAt);


                // 블랙리스트에 추가
                tokenBlacklistService.addTokenToBlacklist(token, expiresAt);

                System.out.println("logout " + tokenBlacklistService);

                return ResponseEntity.ok("Successfully logged out.");
            }
            return ResponseEntity.badRequest().body("Invalid token.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body("Invalid token.");
        }
    }
}

