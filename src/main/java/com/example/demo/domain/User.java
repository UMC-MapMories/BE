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

    @Column(nullable = false)
    private String password;

    private int point;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now(); // 수정일

    private String socialId;
    private String loginType;

    // private String role; // 유저 권한

    // private String username;

}

/*
package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@DynamicInsert
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 자동변환 user_id

    @Column(nullable = false, length = 255)
    private String loginMethod; // 로그인 방법 = Kakao, Google, Normal

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 255)
    private String profileImg; // profile_img

    @Column(nullable = false, length = 50,unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @ColumnDefault("0")
    private Integer point;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now(); // 수정일
}

*/

