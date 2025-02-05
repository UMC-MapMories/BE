package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.jwt.GoogleIdTokenValidator;
import io.jsonwebtoken.Claims;
import java.security.interfaces.RSAPublicKey;

@RestController
@RequestMapping("/join")
public class GoogleController {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public GoogleController(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/Google")
    public ResponseEntity<String> updateProfile(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("X-Google-Authorization");
        System.out.println("GoogleController " + token);


        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token is missing or invalid.");
        }


        try {
            RSAPublicKey googlePublicKey = GoogleIdTokenValidator.getGooglePublicKey(token);

            System.out.println("GoogleController 11111111 " + googlePublicKey);
            Claims claims = GoogleIdTokenValidator.verifyIdToken(token, googlePublicKey);

            System.out.println("GoogleController" + claims);

            // 3. 클레임에서 이메일 추출
            String email = GoogleIdTokenValidator.getEmailFromClaims(claims);
            System.out.println("Extracted Email: " + email);

            // 로그인 = DB에서 이메일로 사용자 조회
            User existingUser = userRepository.findByEmail(email);

            if (existingUser == null) {
                // 사용자 정보가 없다면 회원가입 후 저장
                User newUser = new User();
                newUser.setEmail(email);
                userRepository.save(newUser);

                existingUser = newUser;  // 새로 저장된 사용자로 갱신

                System.out.println("1" + existingUser);
            }

            System.out.println("2" + existingUser);

            // 100년 설정
            String token2 = jwtUtil.createJwt(existingUser.getId(), existingUser.getEmail(), 60 * 60 * 24 * 365 * 100L);

            System.out.println("Generated JWT token: " + token2); // 생성된 JWT 토큰 확인

            // jwt -> header 담아서 응답
            response.addHeader("Authorization", "Bearer " + token2);

            return ResponseEntity.ok("{\"message\":\"login successful\"}");  // 응답을 반환

        } catch (Exception e) {
            // 토큰 검증이나 이메일 추출 중 오류 발생 시
            System.out.println("에러 메세지 " + e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Google ID token.");
        }
    }
}


