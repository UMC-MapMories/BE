package com.example.demo.service;

import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.converter.DiaryConverter;
import com.example.demo.domain.Diary;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.FriendshipStatus;
import com.example.demo.domain.User;
import com.example.demo.dto.DiaryRequestDto;
import com.example.demo.exception.CustomException;
import com.example.demo.repository.DiaryRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

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
    public Optional<Diary> getDiaryByIdAndUserId(Long diaryId) {

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

    public List<Diary> getAllDiaries() { return diaryRepository.findDiariesByIsOpenTrue(); }

    public List<Diary> getFriendDiaries(Long userId) {

        // 친구 목록 조회
        List<Friendship> friends = friendshipRepository.findByUserId(userId, FriendshipStatus.ACCEPTED);

        // 친구 목록에서 User만 추출
        List<User> friendUsers = friends.stream()
                .map(friendship -> (friendship.getFromUser().getId().equals(userId)) ? friendship.getToUser() : friendship.getFromUser())
                .collect(Collectors.toList());

        // 친구 공개인 다이어리 조회
        return diaryRepository.findByIsOpenTrueAndUserIn(friendUsers);
    }
}