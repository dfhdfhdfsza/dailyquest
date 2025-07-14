package com.dailyquest.dailyquest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize   //http요청에 대한 접근제어 설정
                        .requestMatchers(                       //swagger 관련은 인증없이 접근 허용
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/login", "/signup"         //로그인,회원가입
                        ).permitAll()
                        .anyRequest().authenticated()       //그 외 요청은 로그인 필요
                )
                .httpBasic(Customizer.withDefaults());      //http basic 인증방식을 사용


        return http.build();
    }
}
