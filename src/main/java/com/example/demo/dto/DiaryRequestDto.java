package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DiaryRequestDto {
    private Long diaryId;
    private String title;
    private String content;
    private String imgUrl;
    private Boolean isOpen;
    private Boolean isCollaborative;
    private LocalDate date;
}