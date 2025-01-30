package com.example.demo.jwt;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


// POST 방식, /login 경로, 로그인 API 담당하는 filter class
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            // 요청 바디에서 JSON 데이터 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            // 이메일 형식 검증
            if (!isValidEmail(email)) {
                throw new AuthenticationException("유효하지 않은 이메일 형식입니다.") {};
            }

            // email, password 확인
            // System.out.println("Attempting authentication with email: " + email);
            // System.out.println("Attempting authentication with password: " + password);

            // 스프링 시큐리티에서 email, password 검증하기 위해서 token 담기
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

            // 담은 토큰 -> 검증을 위한 AuthenticationManager 전달
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationException("Failed to parse authentication request") {};
        }
    }

    //로그인 성공시 실행하는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        // authentication 객체에서 추출
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal(); // 특정 유저

        // 특정 유저에서 유저 이름, 이메일 추출
        Long id = customUserDetails.getId();
        String email = customUserDetails.getEmail();

        // System.out.println("Authentication successful for user: " + id); // 인증된 사용자 이름

        /*
        // role 값 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        */

        // 100년 설정
        String token = jwtUtil.createJwt(id, email,60 * 60 * 24 * 365 * 100L);


        System.out.println("Generated JWT token: " + token); // 생성된 JWT 토큰 확인

        // jwt -> header 담아서 응답
        response.addHeader("Authorization", "Bearer " + token);

        ResponseEntity.ok("{\"message\":\"login successful\"}");

    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        System.out.println("Authentication failed: " + failed.getMessage()); // 실패 메시지 출력
        response.setStatus(401);
    }

    // 이메일 형식 검증
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
