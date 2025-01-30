/*
package com.example.demo.oauth2;

import com.example.demo.dto.CustomOAuth2User;
import com.example.demo.jwt.JWTUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;


@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // JWTUtil -> CustomSuccesHandler
    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        // uesrname, role값을 받아오기 // jwt 만들 때 role, username을 받아서 만들었으므로
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();


        // token에 username, role값을 전달해서 jwt 생성
        String token = jwtUtil.createJwt(username, role, 60*60*60L);

        // Token 전달 방식
        // Cookie 전달 + 다시 redirect
        response.addCookie(createCookie("Authorization", token));
        response.sendRedirect("http://localhost:3000/");
    }

    // Cookie 생성 메서드
    private Cookie createCookie(String key, String value) {

        // key, value로 쿠키 생성
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/"); // 쿠키 보이는 위치는 전역
        cookie.setHttpOnly(true);

        return cookie; // 변수 반환
    }
}
*/