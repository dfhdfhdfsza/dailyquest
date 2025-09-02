package com.dailyquest.dailyquest.security.limit;

import lombok.Getter;

@Getter
public class RateLimitException extends  RuntimeException{
    private final int httpStatus;   //423 또는 429 권장
    private final String code;
    private final int retryAfterSec;    // 재시도 대기(초)

    public RateLimitException(int httpStatus,String code,String message,int retryAfterSec){
        super(message);
        this.httpStatus=httpStatus;
        this.code=code;
        this.retryAfterSec=retryAfterSec;
    }

    public static RateLimitException ipCooldown(int seconds) {
        return new RateLimitException(429, "IP_COOLDOWN", "요청이 너무 많습니다.", seconds);
    }

    public static RateLimitException loginLocked(int seconds) {
        return new RateLimitException(423, "LOGIN_LOCKED", "로그인 시도가 제한되었습니다.", seconds);
    }
}
