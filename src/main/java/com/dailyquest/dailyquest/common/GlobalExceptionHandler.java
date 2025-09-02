package com.dailyquest.dailyquest.common;

import com.dailyquest.dailyquest.security.limit.RateLimitException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//스프링 전역 예외 처리기
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * @Valid @RequestBody 바인딩(필드) 검증 실패 처리
     * - 대상: DTO 필드 제약 위반(예: @NotBlank, @Size 등)으로 발생한 MethodArgumentNotValidException
     * - 내용: FieldError 목록을 "field: message" 형태로 합쳐 사람이 읽기 쉬운 메시지로 변환
     * - 응답: 400 Bad Request + {"success":false,"code":"VALIDATION_ERROR","message":"..."}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleBodyValidation (MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(ApiResponse.error("VALIDATION_ERROR", msg));
    }
    /**
     * @RequestParam, @PathVariable, @Validated(메서드 파라미터) 등에서 발생하는 제약 위반 처리
     * - 대상: ConstraintViolationException
     * - 특징: 파라미터 단위 검증 실패 메시지들(경로/쿼리 파라미터)을 포함할 수 있음
     * - 응답: 400 Bad Request + {"success":false,"code":"INVALID_REQUEST","message":"..."}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleParamValidation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_REQUEST", ex.getMessage()));
    }
    /**
     * 비즈니스 규칙 위반 등 애플리케이션 정의 예외 처리
     * - 대상: BusinessException (내부에 ErrorCode 보유: HTTP 상태/코드/기본 메시지)
     * - 응답: ErrorCode에 정의된 상태코드 + {"success":false,"code":ErrorCode.code,"message":ex.getMessage()}
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity
                .status(ec.getStatus())
                .body(ApiResponse.error(ec.getCode(), ex.getMessage()));
    }
    /**
     * 마지막 방어선: 처리되지 않은 모든 예외
     * - 대상: 위에서 캐치되지 않은 기타 모든 예외
     * - 동작: 내부 로그로 전체 스택트레이스 기록(운영 모니터링/알림 연동 권장)
     * - 응답: 500 Internal Server Error + 일반화된 에러 메시지(내부 정보 노출 방지)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleEtc(Exception ex) {
        log.error("UNHANDLED EXCEPTION", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "일시적인 오류가 발생했습니다."));
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimit(RateLimitException ex){
        HttpHeaders headers=new HttpHeaders();
        headers.add(HttpHeaders.RETRY_AFTER,String.valueOf(ex.getRetryAfterSec()));
        return  ResponseEntity
                .status(ex.getHttpStatus())
                .headers(headers)
                .body(ApiResponse.error(ex.getCode(),ex.getMessage()));
    }

}
