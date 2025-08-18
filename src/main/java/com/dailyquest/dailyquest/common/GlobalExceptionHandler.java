package com.dailyquest.dailyquest.common;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//스프링 전역 예외 처리기
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    public ResponseEntity<ApiResponse<Void>> handleBodyValidation (MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", msg));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleParamValidation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_REQUEST", ex.getMessage()));
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity
                .status(ec.getStatus())
                .body(ApiResponse.error(ec.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleEtc(Exception ex) {
        log.error("UNHANDLED EXCEPTION", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "일시적인 오류가 발생했습니다."));
    }

}
