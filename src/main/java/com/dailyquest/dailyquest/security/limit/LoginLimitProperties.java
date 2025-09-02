package com.dailyquest.dailyquest.security.limit;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

//application.yml 값이 생성자 바인딩 방식으로 주입
@ConfigurationProperties(prefix = "security.login-limit")
@Validated
public record LoginLimitProperties(
        @Min(60) int accountWindowSeconds,   //계정 단위 실패 집계 창(윈도우) 예: “10분 동안의 실패 횟수”
        @Min(1) int accountSoftThreshold,   //소프트락 임계치 도달하면 임시 제한(예: 캡차 요구/짧은 락/백오프)를 발동
        @Min(60) int accountSoftLockSeconds, //소프트락 지속 시간 이 기간 동안은 사전 체크에서 차단(또는 캡차 요구)
        @Min(1) int accountHardThreshold,   //하드락 임계치 넘으면 더 긴 강한 제한
        @Min(60) int accountHardLockSeconds, //하드락 지속 시간 DB locked_until과 Redis 락 TTL에 모두 반영
        @Min(60) int ipWindowSeconds,    //IP 단위 실패 집계 창
        @Min(1) int ipThreshold,    //IP 과도 실패 임계치 초과 시 IP에 쿨다운(429/캡차 필수 등)
        @Min(60)int ipCooldownSeconds   //IP 쿨다운 지속 시간

) { }
