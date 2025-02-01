package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor // 기본 생성자
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // 알림 유형 (사진, 업데이트 등)
    private String message; // 알림 메시지
    private boolean isRead; // 읽음 여부
    private LocalDateTime createdAt; // 생성 시간
}
