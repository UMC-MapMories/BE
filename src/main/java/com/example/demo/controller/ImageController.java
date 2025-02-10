package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.service.S3FileService;
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

    @PostMapping("/{fileName}")
    public ApiResponse<Map<String, String>> getPresignedUrl(
            @PathVariable(name = "fileName") @Schema(description = "확장자명을 포함해주세요")
            String fileName,
            @RequestParam(name = "contentType") @Schema(description = "파일의 contentType을 입력해주세요")
            String contentType) {

        // Assuming getPresignedUrl returns a Map
        Map<String, String> fileUrl = s3FileService.getPresignedUrl("images", fileName, contentType);

        return ApiResponse.onSuccess(fileUrl);
    }
}
