package com.example.demo.converter;

import com.example.demo.domain.Diary;
import com.example.demo.dto.DiaryRequestDto;
import com.example.demo.dto.DiaryResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class DiaryConverter {
    public static Diary toDiary(DiaryRequestDto diaryRequestDto) {
        Diary diary = new Diary();
        diary.setDiaryId(diaryRequestDto.getDiaryId()); // diaryId 포함
        diary.setCountry(diaryRequestDto.getCountry());
        diary.setDate(diaryRequestDto.getDate());
        diary.setTitle(diaryRequestDto.getTitle());
        diary.setContent(diaryRequestDto.getContent());
        diary.setImgUrl(diaryRequestDto.getImgUrl());
        diary.setIsOpen(diaryRequestDto.getIsOpen());
        diary.setIsCollaborative(diaryRequestDto.getIsCollaborative());
        return diary;
    }

    public static DiaryResponseDto toDto(Diary diary) {
        return DiaryResponseDto.builder()
                .diaryId(diary.getDiaryId())
                .country(diary.getCountry())
                .title(diary.getTitle())
                .content(diary.getContent())
                .imgUrl(diary.getImgUrl())
                .isOpen(diary.getIsOpen())
                .isCollaborative(diary.getIsCollaborative())
                .latitude(diary.getLatitude())
                .longitude(diary.getLongitude())
                .createdAt(diary.getCreatedAt())
                .modifiedAt(diary.getModifiedAt())
                .build();
    }
}