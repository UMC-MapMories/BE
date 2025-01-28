package com.example.demo.repository;

import com.example.demo.domain.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    // 토큰이 블랙리스트에 있는지 확인
    Optional<TokenBlacklist> findByToken(String token);
}
