package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId; // 다이어리 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 작성자 (User 테이블 참조)

    @Column(nullable = false)
    private LocalDateTime date; // 작성 날짜

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    private String imgUrl; // 이미지 URL

    @Column(nullable = false)
    private Boolean isOpen; // 공개 여부 (true: 공개, false: 비공개)

    @Column(nullable = false)
    private Boolean isCollaborative; // 협업 여부 (true: 교환일기, false: 개인 일기)ç

    // @Column(nullable = false, precision = 10, scale = 6)
    @Column(nullable = false)
    private double latitude; // 위도

    // @Column(nullable = false, precision = 10, scale = 6)
    @Column(nullable = false)
    private double longitude; // 경도

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성일

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now(); // 수정일

    @PreUpdate
    public void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }
}
