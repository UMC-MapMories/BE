package com.example.demo.jwt;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;


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

        // 100년 설정
        String token = jwtUtil.createJwt(id, email,60 * 60 * 24 * 365 * 100L);

        // jwt -> header 담아서 응답
        response.addHeader("Authorization", "Bearer " + token);

        ApiResponse<String> apiResponse = ApiResponse.onSuccess(null);


    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        // 실패 응답 생성 (ErrorStatus._UNAUTHORIZED 활용)
        ErrorStatus errorStatus = ErrorStatus.LOGIN_FAILED;

        // 응답 상태 코드 설정 (NPE 방지)
        if (errorStatus.getHttpStatus() != null) {
            response.setStatus(errorStatus.getHttpStatus().value());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 기본값 설정
        }

        // JSON 응답 생성
        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.onFailure(
                errorStatus.getCode(),
                errorStatus.getMessage(),
                null
        );

        // JSON 응답을 클라이언트로 전송
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }

    // 이메일 형식 검증
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
