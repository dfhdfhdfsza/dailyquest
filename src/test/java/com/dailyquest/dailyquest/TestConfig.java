package com.dailyquest.dailyquest;

import com.dailyquest.dailyquest.security.limit.LoginEndpointRateLimitFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

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

    @Bean
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        // 빈 레지스트리여도 SecurityFilterChain 빌드는 통과합니다.
        return new InMemoryClientRegistrationRepository();
    }
}
