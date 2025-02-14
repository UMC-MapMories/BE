package com.example.demo.service;

import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.converter.DiaryConverter;
import com.example.demo.domain.Diary;
import com.example.demo.domain.User;
import com.example.demo.dto.DiaryRequestDto;
import com.example.demo.exception.CustomException;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    // 다이어리 작성
    public Diary createDiary(DiaryRequestDto diaryRequestDto, Long userId) {
        if (!StringUtils.hasText(diaryRequestDto.getCountry()) ||
                !StringUtils.hasText(diaryRequestDto.getTitle()) ||
                diaryRequestDto.getIsOpen() == null ||
                diaryRequestDto.getIsCollaborative() == null ||
                diaryRequestDto.getDate() == null) {
            throw new CustomException(ErrorStatus.MISSING_ESSENTIAL_ELEMENTS.getMessage(),
                    ErrorStatus.MISSING_ESSENTIAL_ELEMENTS.getHttpStatus().value());
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND.getMessage(),
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus().value()));

        // Diary 엔티티 생성 및 설정
        Diary diary = DiaryConverter.toEntity(diaryRequestDto, user);

        // 다이어리 저장
        return diaryRepository.save(diary);
    }

    // 특정 다이어리 조회
    public Optional<Diary> getDiaryByIdAndUserId(Long diaryId, Long userId) {
        // 친구 공개나 비공개 게시물 조회 시의 로직은 추후 추가

        Optional<Diary> diary = diaryRepository.findById(diaryId);

        // 다이어리 찾지 못했을 경우 예외 발생
        if (diary.isEmpty()) {
            throw new CustomException(ErrorStatus.DIARY_NOT_FOUND.getMessage(), ErrorStatus.DIARY_NOT_FOUND.getHttpStatus().value());
        }

        return diary;
    }

    // 다이어리 삭제
    public void deleteDiary(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(ErrorStatus.DIARY_NOT_FOUND.getMessage(), ErrorStatus.DIARY_NOT_FOUND.getHttpStatus().value()));
        diaryRepository.delete(diary);
    }

    // 사용자가 작성한 전체 다이어리 목록 조회
    public List<Diary> getAllDiariesByUserId(Long userId) {
        return diaryRepository.findDiaryByUserId(userId);
    }
}