package com.example.demo.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.example.demo.apiPayload.code.BaseErrorCode;
import com.example.demo.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "사용자가 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4002", "사용자가 이미 존재합니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4003", "닉네임은 필수 입니다."),
    LOGIN_ID_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER4004", "이미 존재하는 로그인 ID입니다.");

    // 토큰 관련 에러
    INVALID_TOKEN(HttpStatus.BAD_REQUEST,"AUTH4001", "잘못된 토큰입니다."),
    //다이어리 관련 에러
    DIARY_NOT_FOUND(HttpStatus.BAD_REQUEST,"DIARY4001", "다이어리가 없습니다."),
    MISSING_ESSENTIAL_ELEMENTS(HttpStatus.BAD_REQUEST,"DIARY4002", "다이어리 필수 항목이 누락되었습니다."),
    INVALID_USER(HttpStatus.BAD_REQUEST,"DIARY4003", "사전 저장된 유저 아이디와 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}