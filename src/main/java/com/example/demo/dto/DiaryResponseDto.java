package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryResponseDto {
    private Long diaryId;
    private String country;
    private String title;
    private String content;
    private String imgUrl;
    private Boolean isOpen;
    private Boolean isCollaborative;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
