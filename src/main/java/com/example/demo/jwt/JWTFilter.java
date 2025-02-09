package com.example.demo.jwt;

import com.example.demo.domain.User;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 검증, SecurityContextHolder 세션 생성
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public JWTFilter(JWTUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }


    // jwt 토큰을 어디서 가져올지
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request -> Authorization 헤더를 찾기
        String authorization= request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); //filter 종료, req,resp 넘겨주기 -> 다음 filter
            return;
        }

        String token = authorization.split(" ")[1];

        // 토큰이 블랙리스트에 있는지 확인
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has been logged out.");
            return;
        }

        //토큰에서 email 휙득
        String email = jwtUtil.getEmail(token);

        //userEntity 생성 -> 값 set
        User userEntity = new User();
        userEntity.setEmail(email);
        userEntity.setPassword("temppassword");

        //UserDetails 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        System.out.println("JWTFILTER" + authToken); // 정상동작 -> controller

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response); // 다음 filter req, res 전달
    }
}
