package com.example.demo.controller;

import com.example.demo.dto.DiaryLocationRequestDto;
import com.example.demo.dto.DiaryLocationResponseDto;
import com.example.demo.service.DiaryLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping
public class DiaryLocationController {

    private final DiaryLocationService diaryLocationService;

    // 다이어리 위치 저장
    @PostMapping
    public ResponseEntity<DiaryLocationResponseDto> saveLocation(@RequestBody DiaryLocationRequestDto requestDto){
        DiaryLocationResponseDto savedLocation = diaryLocationService.saveDiaryLocation(requestDto);
        return ResponseEntity.ok(savedLocation);
    }

    // 공개된 다이어리 위치 조회
    @GetMapping("/public")
    public ResponseEntity<List<DiaryLocationResponseDto>> getAllPublicLocation() {
        List<DiaryLocationResponseDto> locations = diaryLocationService.getAllpublicDiaryLocation();
        return ResponseEntity.ok(locations);
    }
}
