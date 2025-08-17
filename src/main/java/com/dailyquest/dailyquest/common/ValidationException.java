package com.dailyquest.dailyquest.common;

import org.springframework.validation.BindingResult;

//입력값 검증 실패를 표현하는 커스텀 예외
public class ValidationException extends  RuntimeException{
    public ValidationException(BindingResult br) {
        super(br.getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a,b)-> a + ", " + b).orElse("Invalid request"));
    }
}
