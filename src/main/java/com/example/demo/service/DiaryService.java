package com.example.demo.service;

import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.domain.Diary;
import com.example.demo.exception.handler.DiaryHandler;
import com.example.demo.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    // 다이어리 작성
    public Diary createDiary(Diary diary, Long userId) {
        // 다이어리 ID로 기존 다이어리 조회
        Optional<Diary> existingDiaryOpt = diaryRepository.findById(diary.getDiaryId());

        if (existingDiaryOpt.isEmpty()) {
            throw new DiaryHandler(ErrorStatus.DIARY_NOT_FOUND);
        }

        Diary existingDiary = existingDiaryOpt.get();

        //다이어리 상 저장된 유저와 요청 유저의 id가 다를 경우 예외 발생
        if(!existingDiary.getUser().getUserId().equals(userId)){
            throw new DiaryHandler(ErrorStatus.INVALID_USER);
        }

        // 필수 항목 누락시 예외 발생
        if (diary.getDate() == null || diary.getTitle() == null || diary.getContent() == null
                || diary.getIsOpen() == null || diary.getIsCollaborative() == null) {
            throw new DiaryHandler(ErrorStatus.MISSING_ESSENTIAL_ELEMENTS);
        }

        // 다이어리 정보 저장
        existingDiary.setDate(diary.getDate());
        existingDiary.setTitle(diary.getTitle());
        existingDiary.setContent(diary.getContent());
        existingDiary.setImgUrl(diary.getImgUrl());
        existingDiary.setIsOpen(diary.getIsOpen());
        existingDiary.setIsCollaborative(diary.getIsCollaborative());

        // 다이어리 저장 (업데이트)
        return diaryRepository.save(existingDiary);
    }

    // 특정 다이어리 조회
    public Optional<Diary> getDiaryByIdAndUserId(Long diaryId, Long userId) {
        //친구 공개나 비공개 게시물 조회 시의 로직은 추후 추가

        Optional<Diary> diary = diaryRepository.findById(diaryId);

        // You can throw an exception if the diary is not found
        if (diary.isEmpty()) {
            throw new DiaryHandler(ErrorStatus.DIARY_NOT_FOUND);
        }

        return diary;
    }

    // 다이어리 삭제
    public boolean deleteDiary(Long diaryId) {
        Optional<Diary> diaryOpt = diaryRepository.findById(diaryId);
        if (diaryOpt.isPresent()) {
            diaryRepository.delete(diaryOpt.get());
            return true;
        }
        return false;
    }

    // 사용자가 작성한 전체 다이어리 목록 조회
    public List<Diary> getAllDiariesByUserId(Long userId) {
        return diaryRepository.findDiaryByUserId(userId);
    }
}
