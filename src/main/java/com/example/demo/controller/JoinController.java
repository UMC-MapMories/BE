package com.example.demo.controller;

import com.example.demo.apiPayload.ApiResponse;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.dto.JoinDTO;
import com.example.demo.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@ResponseBody
// 회원가입
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public ApiResponse<String> joinProcess(@RequestBody JoinDTO joinDTO) {
        try {
            joinService.joinProcess(joinDTO);
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(),ErrorStatus._INTERNAL_SERVER_ERROR.getMessage(), null);
        }

    }
}
