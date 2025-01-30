package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.CustomUserDetails;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User userData = userRepository.findByEmail(email);

        System.out.println(userData);

        // userData 조회한 데이터 기반 검증
        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }
}