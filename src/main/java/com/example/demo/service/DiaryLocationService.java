package com.example.demo.service;


import com.example.demo.domain.Diary;
import com.example.demo.dto.DiaryLocationRequestDto;
import com.example.demo.dto.DiaryLocationResponseDto;
import com.example.demo.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DiaryLocationService {

    private final DiaryRepository diaryRepository;

    // 다이어리 위치 저장
    public DiaryLocationResponseDto saveDiaryLocation(DiaryLocationRequestDto requestDto) {
        // 다이어리 조회
        Diary diary = diaryRepository.findByDiaryId(requestDto.getDiaryId());
        if (diary == null) {
            throw new IllegalArgumentException("이 아이디로 발견된 다이어리가 없습니다. : " + requestDto.getDiaryId());
        }

        // 위도와 경도 업데이트
        diary.setLatitude(requestDto.getLatitude());
        diary.setLongitude(requestDto.getLongitude());
        Diary savedDiary = diaryRepository.save(diary);

        return new DiaryLocationResponseDto(
                savedDiary.getDiaryId(),
                savedDiary.getLatitude(),
                savedDiary.getLongitude()
        );
    }

    public List<DiaryLocationResponseDto> getAllpublicDiaryLocation() {
        List<Diary> publicDiaries = diaryRepository.findByIsOpenTrue();
        return publicDiaries.stream()
                .map(diary -> new DiaryLocationResponseDto(
                        diary.getDiaryId(),
                        diary.getLatitude(),
                        diary.getLongitude()
                ))
                .collect(Collectors.toList());
    }
}
