package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryLocationResponseDto {

    private Long diaryId; //다이어리 아이디
    private double latitude; // 위도
    private double longitude; // 경도
}
