package com.example.demo.service;

import com.example.demo.domain.TokenBlacklist;
import com.example.demo.repository.TokenBlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    public TokenBlacklistService(TokenBlacklistRepository tokenBlacklistRepository) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    // 토큰을 블랙리스트에 추가
    public void addTokenToBlacklist(String token, LocalDateTime expiresAt) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist(token, expiresAt);
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        Optional<TokenBlacklist> blacklistToken = tokenBlacklistRepository.findByToken(token); // findbytoken이 sql query문으로 변환
        // System.out.println("3" + blacklistToken);
        // optional -> 있으면 tokenBlacklist type

        return blacklistToken.isPresent();
    }
}

