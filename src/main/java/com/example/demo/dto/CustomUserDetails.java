package com.example.demo.dto;

import com.example.demo.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User userEntity;

    public CustomUserDetails(User userEntity) {
        this.userEntity = userEntity;
    }


    // 권한을 반환 x
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }


    @Override
    public String getPassword() {

        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return "";
    }

    public Long getId() {
        return userEntity.getId();
    }

    public String getName() {
        return userEntity.getName();
    }

    public String getProfileImg() {
        return userEntity.getProfileImg();
    }


    // 이메일을 반환하는 메서드
    public String getEmail() {
        return userEntity.getEmail();
    }


    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}