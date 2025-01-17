package com.example.demo.repository;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // 이메일로 사용자를 찾을 수 있도록 UserRepository 작성
}

// save() 메서드를 통해 User 객체를 데이터베이스에 저장