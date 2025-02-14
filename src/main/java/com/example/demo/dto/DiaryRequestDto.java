package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DiaryRequestDto {
    private String country;
    private String title;
    private String content;
    private String imgUrl;
    private Boolean isOpen;
    private Boolean isCollaborative;
    private double latitude;
    private double longitude;
    private LocalDate date;
}