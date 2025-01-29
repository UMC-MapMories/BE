package com.example.demo.exception.handler;

import com.example.demo.apiPayload.code.BaseErrorCode;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.GeneralException;

public class DiaryHandler extends GeneralException {

    public DiaryHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

