package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.domain.User;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import com.example.demo.jwt.KakaoIdTokenValidator;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/join")
public class KaKaoController {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public KaKaoController(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/Kakao")
    public ApiResponse<String> updateProfile(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("X-Kakao-Authorization");

        if (token == null) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        try {
            // ID 토큰 검증
            Claims claims = KakaoIdTokenValidator.verifyIdToken(token);

            // 클레임에서 이메일 추출
            String email = KakaoIdTokenValidator.getEmailFromClaims(claims);

            // DB에서 이메일로 사용자 조회
            User existingUser = userRepository.findByEmail(email);

            if (existingUser == null) {
                // 사용자 정보가 없다면 회원가입 후 저장
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setPassword("default");
                userRepository.save(newUser);

                existingUser = newUser; // 새로 저장된 사용자로 갱신
            }

            // 100년 설정
            String token2 = jwtUtil.createJwt(existingUser.getId(), existingUser.getEmail(), 60 * 60 * 24 * 365 * 100L);

            // jwt -> header 담아서 응답
            response.addHeader("Authorization", "Bearer " + token2);

            return ApiResponse.onSuccess(null);

        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(),ErrorStatus._INTERNAL_SERVER_ERROR.getMessage(), null);
        }
    }
}

