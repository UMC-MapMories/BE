package com.example.demo.dto;

import lombok.Getter;

@Getter
public class DiaryLocationRequestDto {

    private Long diaryId; // 다이어리 ID
    private double latitude; // 위도
    private double longitude; // 경도
}
