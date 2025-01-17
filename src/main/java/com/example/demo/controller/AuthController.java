package com.example.demo.controller;

// 로그인 요청을 처리할 컨트롤러

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.config.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    // 클라이언트가 회원가입 요청을 보냅니다. 요청에는 email과 password가 포함
    // AuthController는 UserService에 요청을 보내 사용자 정보를 데이터베이스에 저장
    // 사용자가 입력한 비밀번호는 BCryptPasswordEncoder를 통해 암호화되어 저장
    //
    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password)) // 비밀번호 암호화
                .build();
        userService.save(user);
        return "Registration successful"; // 저장이 완료되면 "Registration successful" 메시지가 클라이언트에 반환
    }

    // 로그인
    // 클라이언트가 로그인 요청을 보냅니다. 요청에는 email과 password가 포함
    // UserService는 이메일로 사용자 조회 후, 입력된 비밀번호와 데이터베이스에 저장된 비밀번호를 비교
    // 비밀번호가 일치하면, JwtTokenProvider를 사용하여 JWT 토큰을 생성

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        try {
            User user = userService.login(email, password);
            String token = jwtTokenProvider.generateToken(email);
            return "Login successful, Token: " + token; // 생성된 JWT 토큰이 클라이언트에 반환
        } catch (Exception e) {
            return "Invalid credentials";
        }
    }

    // 로그인 후 받은 JWT 토큰은 클라이언트에서 요청을 보낼 때마다 Authorization 헤더에 포함

    // 로그아웃
    @PostMapping("/logout")
    public String logout() {
        return "Logout successful";
    }
}



