package com.example.demo.jwt;

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

// POST 방식 /login 경로를 매핑하는 로그인 API 담당하는 filter class
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    // 인자 = 응답, 요청
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            // 요청 바디에서 JSON 데이터 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class); // LoginRequest는 JSON 형식에 맞는 DTO 클래스

            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            // 검증 로직 여기에 추가
            // 실패되면 에러 발생 -> 400 error


            // email, password 확인
            System.out.println("Attempting authentication with email: " + email);
            System.out.println("Attempting authentication with password: " + password);

            // 스프링 시큐리티에서 email, password 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

            // token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationException("Failed to parse authentication request") {};
        }
    }

    /*
    // 인자 = 응답, 요청
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 email, password 추출
        // String email = obtainUsername(request);

        String email = request.getParameter("email"); // email 기준으로 추출
        String password = obtainPassword(request);

        // email, password 확인
        System.out.println("Attempting authentication with email: " + email);
        System.out.println("Attempting authentication with password: " + password);

        //스프링 시큐리티에서 email, password 검증하기 위해서는 token에 담아야 함

        // email, password, role 값 객체에 담아서 전달 -> 이 과정이 DTO에 담아서 전달
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }
    */


    //로그인 성공시 실행하는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        // authentication 객체에서 추출
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal(); // 특정 유저

        Long id = customUserDetails.getId(); // 특정 유저에서 유저 이름 뽑아내기
        // String name = customUserDetails.getName(); // 특정 유저에서 유저 이름 뽑아내기
        // String profileImg = customUserDetails.getProfileImg(); // 특정 유저에서 유저 이름 뽑아내기
        String email = customUserDetails.getEmail();

        System.out.println("Authentication successful for user: " + id); // 인증된 사용자 이름 확인


        /*
        // role 값 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        */

        System.out.println("Authentication successful for user: " + id); // 인증된 사용자 이름 확인

        // String token = jwtUtil.createJwt(email, role, 60*60*10L);
        String token = jwtUtil.createJwt(id, email,60 * 60 * 24 * 365 * 100L);  // 100년 설정



        System.out.println("Generated JWT token: " + token); // 생성된 JWT 토큰 출력

        response.addHeader("Authorization", "Bearer " + token); // jwt -> header 담아서 응답
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        System.out.println("Authentication failed: " + failed.getMessage()); // 실패 메시지 출력
        response.setStatus(401);
    }
}


