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
        // System.out.println(LocalDateTime.now());
        // return ResponseEntity.ok("123");

        // db 관련 <-> try catch // try안에서 throw -> catch // 받는다,,
        // catch가 안되면 500 응답 -> 종료된다.

        try {
            System.out.println("1");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                System.out.println("2");

                String token = authorization.split(" ")[1];  // "Bearer " 이후의 토큰 부분 추출

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

// 500 -> 응답이 정상적이지 못하게 종료,, 자동으로
