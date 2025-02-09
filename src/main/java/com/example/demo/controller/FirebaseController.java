package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.domain.User;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.repository.UserRepository;
import com.example.demo.jwt.FirebaseTokenVerifierService;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/join")
public class FirebaseController {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final FirebaseTokenVerifierService tokenVerifierService;

    @Value("${firebase.projectId}")
    private String firebaseProjectId;

    public FirebaseController(UserRepository userRepository, JWTUtil jwtUtil, FirebaseTokenVerifierService tokenVerifierService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.tokenVerifierService = tokenVerifierService;
    }

    @PostMapping("/Google")
    public ApiResponse<String> updateProfile(HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader("X-Google-Authorization");

        if (token == null || token.trim().isEmpty()) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }
        try {
            // Firebase 토큰 검증 후 클레임 세트 반환
            JWTClaimsSet claims = tokenVerifierService.verifyIdToken(token, firebaseProjectId);

            // 클레임에서 이메일 추출
            String email = FirebaseTokenVerifierService.getEmailFromClaims(claims);

            // 로그인: DB에서 이메일로 사용자 조회
            User existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                // 사용자 정보가 없다면 회원가입 후 저장
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setPassword("default");

                userRepository.save(newUser);
                existingUser = newUser;
            }

            // JWT 생성 (예, 100년 만료)
            String token2 = jwtUtil.createJwt(existingUser.getId(), existingUser.getEmail(), 60 * 60 * 24 * 365 * 100L);
            response.addHeader("Authorization", "Bearer " + token2);

            return ApiResponse.onSuccess(null);

        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(),ErrorStatus._INTERNAL_SERVER_ERROR.getMessage(), null);
        }
    }
}



