package com.example.demo.service;

import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.domain.Diary;
import com.example.demo.dto.DiaryRequestDto;
import com.example.demo.exception.CustomException;
import com.example.demo.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    // 다이어리 작성
    public Diary createDiary(DiaryRequestDto diaryRequestDto, Long userId) {
        // 1. 다이어리 ID로 기존 다이어리 조회
        Optional<Diary> existingDiaryOpt = diaryRepository.findById(diaryRequestDto.getDiaryId());

        if (existingDiaryOpt.isEmpty()) {
            throw new CustomException(ErrorStatus.DIARY_NOT_FOUND.getMessage(), ErrorStatus.DIARY_NOT_FOUND.getHttpStatus().value());
        }

        Diary existingDiary = existingDiaryOpt.get();

        // 2. 다이어리 상 저장된 유저와 요청 유저의 id가 다를 경우 예외 발생
        if (!existingDiary.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorStatus.INVALID_USER.getMessage(), ErrorStatus.INVALID_USER.getHttpStatus().value());
        }

        // 3. 필수 항목 누락시 예외 발생
        if (diaryRequestDto.getDate() == null || diaryRequestDto.getTitle() == null || diaryRequestDto.getContent() == null
                || diaryRequestDto.getIsOpen() == null || diaryRequestDto.getIsCollaborative() == null) {
            throw new CustomException(ErrorStatus.MISSING_ESSENTIAL_ELEMENTS.getMessage(),
                    ErrorStatus.MISSING_ESSENTIAL_ELEMENTS.getHttpStatus().value());
        }

        // 4. 다이어리 정보 업데이트
        existingDiary.setDate(diaryRequestDto.getDate());
        existingDiary.setTitle(diaryRequestDto.getTitle());
        existingDiary.setContent(diaryRequestDto.getContent());
        existingDiary.setImgUrl(diaryRequestDto.getImgUrl());
        existingDiary.setIsOpen(diaryRequestDto.getIsOpen());
        existingDiary.setIsCollaborative(diaryRequestDto.getIsCollaborative());
        existingDiary.setModifiedAt(LocalDateTime.now());

        // 5. 다이어리 저장 (업데이트)
        return diaryRepository.save(existingDiary);
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
