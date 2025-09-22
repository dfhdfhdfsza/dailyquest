package com.dailyquest.dailyquest;

import com.dailyquest.dailyquest.security.limit.LoginEndpointRateLimitFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public LoginEndpointRateLimitFilter loginEndpointRateLimitFilter() {
        // 의존성 많아도 상관없이 Mock으로 대체
        return mock(LoginEndpointRateLimitFilter.class);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return mock(StringRedisTemplate.class);
    }
}
