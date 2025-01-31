package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.domain.Diary;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.service.DiaryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private JWTUtil jwtUtil;

    // 다이어리 작성
    @PostMapping
    public ApiResponse<Diary> createDiary(@RequestBody Diary diary, HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(),ErrorStatus.INVALID_TOKEN.getMessage(),null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        // 2. 다이어리 저장
        Diary createdDiary = diaryService.createDiary(diary, userId);
        return ApiResponse.onSuccess(createdDiary);
    }

    // 다이어리 상세 조회 (특정 다이어리)
    @GetMapping("/{diaryId}")
    public ApiResponse<Diary> getDiary(@PathVariable Long diaryId, HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(),ErrorStatus.INVALID_TOKEN.getMessage(),null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        // 2. 다이어리 조회
        Optional<Diary> diary = diaryService.getDiaryByIdAndUserId(diaryId, userId);

        return ApiResponse.onSuccess(diary.get());
    }

    // 다이어리 삭제
    @DeleteMapping("/{diaryId}")
    public ApiResponse<Void> deleteDiary(@PathVariable Long diaryId, HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(),ErrorStatus.INVALID_TOKEN.getMessage(),null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        // 2. 다이어리 삭제
        diaryService.deleteDiary(diaryId);
        return ApiResponse.onSuccess(null);  // 성공 시 응답 반환
    }

    // 다이어리 리스트 조회
    @GetMapping
    public ApiResponse<List<Diary>> getAllDiaries(HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(),ErrorStatus.INVALID_TOKEN.getMessage(),null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        // 2. 유저의 모든 다이어리 조회
        List<Diary> diaries = diaryService.getAllDiariesByUserId(userId);

        return ApiResponse.onSuccess(diaries);
    }
}

