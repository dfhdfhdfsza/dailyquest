package com.dailyquest.dailyquest.common;

//도메인/비즈니스 로직에서 발생하는 예외를 표현하기 위해 만든 커스텀 예외
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException 메시지에도 기본 메시지 세팅
        this.errorCode = errorCode;
    }
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}


