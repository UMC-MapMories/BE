package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.domain.Diary;
import com.example.demo.exception.handler.DiaryHandler;
import com.example.demo.service.DiaryService;
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
    private UserService userService;

    // 다이어리 작성
    @PostMapping
    public ApiResponse<Diary> createDiary(@RequestBody Diary diary, @RequestHeader("Authorization") String authorization) {
        //토큰으로부터 유저 id 추출
        String accessToken = authorization.replace("Bearer ", "");
        Optional<Long> userIdOpt = userService.getUserIdFromToken(accessToken);
        Long userId = userIdOpt.get();
        //위 항목 추후 user 기능 추가 후 수정될 수 있음

        //다이어리 저장
        Diary createdDiary = diaryService.createDiary(diary, userId);
        return ApiResponse.onSuccess(createdDiary);
    }
    // 다이어리 상세 조회 (특정 다이어리)
    @GetMapping("/{diaryId}")
    public ApiResponse<Diary> getDiary(@PathVariable Long diaryId, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace("Bearer ", "");
        Optional<Long> userIdOpt = userService.getUserIdFromToken(accessToken);
        Long userId = userIdOpt.get();

        Optional<Diary> diary = diaryService.getDiaryByIdAndUserId(diaryId,userId);

        return ApiResponse.onSuccess(diary.get());
    }

    // 다이어리 삭제
    @DeleteMapping("/{diaryId}")
    public ApiResponse<Void> deleteDiary(@PathVariable Long diaryId, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace("Bearer ", "");
        Optional<Long> userIdOpt = userService.getUserIdFromToken(accessToken);

        boolean isDeleted = diaryService.deleteDiary(diaryId);

        if (isDeleted) {
            return ApiResponse.onSuccess(null);
        } else {
            return ApiResponse.onFailure("DIARY_002", "Failed to delete diary", null);
        }
    }

    // 다이어리 리스트 조회
    @GetMapping
    public ApiResponse<List<Diary>> getAllDiaries(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace("Bearer ", "");
        Optional<Long> userIdOpt = userService.getUserIdFromToken(accessToken);

        List<Diary> diaries = diaryService.getAllDiariesByUserId(userIdOpt.get());
        return ApiResponse.onSuccess(diaries);
    }
}

