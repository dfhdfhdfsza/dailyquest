package com.dailyquest.dailyquest.common;

import org.springframework.validation.BindingResult;

//도메인/비즈니스 로직에서 발생하는 예외를 표현하기 위해 만든 커스텀 예외
public class BusinessException extends RuntimeException {
    private final String code;
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    public String getCode() { return code; }
}


