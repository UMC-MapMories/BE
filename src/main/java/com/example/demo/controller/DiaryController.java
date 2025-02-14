package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.domain.Diary;
import com.example.demo.dto.DiaryRequestDto;
import com.example.demo.dto.DiaryResponseDto;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.converter.DiaryConverter;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @Autowired
    private JWTUtil jwtUtil;

    // 다이어리 작성
    @Operation(summary = "다이어리 작성", description = "다이어리와 위치 정보를 작성하는 API")
    @PostMapping
    public ApiResponse<DiaryResponseDto> createDiary(@RequestBody DiaryRequestDto diaryRequestDto, HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        // 2. 다이어리 생성
        Diary updatedDiary = diaryService.createDiary(diaryRequestDto, userId);

        // 3. DiaryResponseDto로 변환
        DiaryResponseDto diaryResponseDto = DiaryConverter.toDto(updatedDiary);

        // 4. ApiResponse에 결과 반환
        return ApiResponse.onSuccess(diaryResponseDto);  // DiaryResponseDto로 반환
    }

    // 다이어리 상세 조회 (특정 다이어리)
    @Operation(summary = "다이어리 상세 조회", description = "특정 다이어리에 대해 상세하게 조회하는 API")
    @GetMapping("/{diaryId}")
    public ApiResponse<DiaryResponseDto> getDiary(@PathVariable Long diaryId, HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        // 2. 다이어리 조회
        Optional<Diary> diary = diaryService.getDiaryByIdAndUserId(diaryId, userId);
        DiaryResponseDto diaryDto = DiaryConverter.toDto(diary.get());
        return ApiResponse.onSuccess(diaryDto);
    }

    // 다이어리 삭제
    @Operation(summary = "다이어리 삭제", description = "특정 다이어리를 삭제하는 API")
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
    @Operation(summary = "전체 다이어리 목록 반환", description = "사용자가 작성한 다이어리 전체 목록을 리스트로 반환")
    @GetMapping
    public ApiResponse<List<DiaryResponseDto>> getAllDiaries(HttpServletRequest request) {
        // 1. JWT 토큰에서 사용자 ID 추출
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_TOKEN.getCode(), ErrorStatus.INVALID_TOKEN.getMessage(), null);
        }

        String jwtToken = token.split(" ")[1];
        Long userId = jwtUtil.getId(jwtToken);

        // 2. 유저의 모든 다이어리 조회
        List<Diary> diaries = diaryService.getAllDiariesByUserId(userId);

        // 3. Diary 리스트를 DiaryResponseDto 리스트로 변환
        List<DiaryResponseDto> diaryResponseDtos = diaries.stream()
                .map(DiaryConverter::toDto)
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(diaryResponseDtos);
    }
}
