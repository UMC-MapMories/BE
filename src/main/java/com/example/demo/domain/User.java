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
@DynamicInsert // insert, update 시 null 인 경우는 그냥 쿼리를 보내지 않도록 하기
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 자동변환 user_id

    // socialId -> 칼럼도 따로 필요...?

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
