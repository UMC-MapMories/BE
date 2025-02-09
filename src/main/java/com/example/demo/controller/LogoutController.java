package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ApiResponse<String> logout(@RequestHeader("Authorization") String authorization ) {

        try {
            if (authorization != null && authorization.startsWith("Bearer ")) {

                String token = authorization.split(" ")[1];

                // JWT 토큰의 만료 시간 확인
                LocalDateTime expiresAt = jwtUtil.getExpiration(token);

                // 블랙리스트에 추가
                tokenBlacklistService.addTokenToBlacklist(token, expiresAt);

                return ApiResponse.onSuccess(null);
            }
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(),ErrorStatus._INTERNAL_SERVER_ERROR.getMessage(), null);
        }
    }
}

