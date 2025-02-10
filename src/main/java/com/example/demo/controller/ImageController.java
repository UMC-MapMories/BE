package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.service.S3FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("image")
public class ImageController {

    private final S3FileService s3FileService;

    @Autowired
    public ImageController(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    @Operation(summary = "이미지 업로드 url 발급", description = "이미지를 업로드 할 링크를 발급 받는 API, result의 url로 업로드 진행")
    @PostMapping
    public ApiResponse<Map<String, String>> getPresignedUrl(
            @RequestParam(name = "fileName") @Schema(description = "확장자명을 포함해주세요")
            String fileName,
            @RequestParam(name = "contentType") @Schema(description = "파일의 contentType을 입력해주세요 Ex) image/png")
            String contentType) {

        // Assuming getPresignedUrl returns a Map
        Map<String, String> fileUrl = s3FileService.getPresignedUrl("images", fileName, contentType);

        return ApiResponse.onSuccess(fileUrl);
    }
}
