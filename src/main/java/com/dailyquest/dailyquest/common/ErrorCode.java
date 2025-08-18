package com.dailyquest.dailyquest.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 공통
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),

    // 인증/인가
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "인증이 필요합니다."),
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_FORBIDDEN", "권한이 없습니다."),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    AUTH_TOKEN_PURPOSE_MISMATCH(HttpStatus.FORBIDDEN,"AUTH_TOKEN_PURPOSE_MISMATCH","토큰 목적이 올바르지 않습니다."),

    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_DUPLICATE_ID(HttpStatus.CONFLICT, "USER_DUPLICATE_ID", "이미 사용 중인 아이디입니다."),
    USER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),

    // 비밀번호
    PASSWORD_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "PASSWORD_POLICY_VIOLATION", "비밀번호 규칙 위반"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다."),
    // 메일
    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL_SEND_FAILED", "메일 전송에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }


}
