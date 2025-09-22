package com.dailyquest.dailyquest;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
@TestConfiguration
public class TestConfig {

    @Bean
    RedisTemplate<?,?> redisTemplate() {
        return Mockito.mock(RedisTemplate.class);
    }
}
