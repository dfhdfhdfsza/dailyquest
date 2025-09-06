package com.dailyquest.dailyquest.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialAuthCodeService {
    private final StringRedisTemplate redis;

    public String issueAuthCode(Long userId){
        String code= UUID.randomUUID().toString().replace("-","");
        String key="social:code:"+code;
        //60초 TTL
        redis.opsForValue().set(key,String.valueOf(userId), Duration.ofSeconds(60));
        return code;
    }

    public Long consume(String code){
        String key="social:code:"+code;
        String v=redis.opsForValue().get(key);
        if(v==null)return null;
        redis.delete(key);
        return Long.valueOf(v);
    }
}
