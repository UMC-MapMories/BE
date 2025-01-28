package com.example.demo.repository;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Boolean existsByUsername(String email);
    Boolean existsByEmail(String email);

    // User findByUsername(String email);
    User findByEmail(String email);  // username 대신 email 수정

    // UserEntity findByUsername(String username);
}


