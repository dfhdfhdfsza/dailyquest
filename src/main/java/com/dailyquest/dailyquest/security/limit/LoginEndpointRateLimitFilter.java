package com.dailyquest.dailyquest.security.limit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class LoginEndpointRateLimitFilter extends OncePerRequestFilter {

    private final  LoginAttemptLimiter limiter;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        return !("POST".equalsIgnoreCase(request.getMethod())
        &&request.getRequestURI().equals("/api/auth/login"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String ip= limiter.extractClientIp(request);

        //IP 잠금만 빠르게 확인(엔드포인트 레이트리밋 역할)
        try {
            limiter.preCheck("__noop__",ip);    //계정은 무시되도록 특수값
            chain.doFilter(request,response);
        }catch (RateLimitException ex){
            response.setStatus(ex.getHttpStatus());
            response.setHeader(HttpHeaders.RETRY_AFTER,String.valueOf(ex.getRetryAfterSec()));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                {"success":false,"data":null,"message":"요청이 너무 많습니다. 잠시 후 다시 시도하세요.","code":"IP_COOLDOWN"}
            """);
        }
    }
}
