package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 프로필 이름

    private String profileImg; // 프로필 이미지 경로

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    private int point;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now(); // 수정일

    // private String socialId;
    // private String loginType;
    // private String role;
}

