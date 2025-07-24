package com.dailyquest.dailyquest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity  //Spring Security 설정을 사용자 정의로 오버라이드
public class SecurityConfig {
//    @Autowired
//    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth   //http요청에 대한 접근제어 설정
                        .requestMatchers(                       //swagger 관련은 인증없이 접근 허용
                                "/swagger-ui/**","/swagger-resources/**",
                                "/v3/api-docs/**","/css/**","/api/**","/js/**",
                                "/","/login", "/signup","/join"         //로그인,회원가입
                        ).permitAll()
                        .anyRequest().authenticated()       //그 외 요청은 로그인 필요
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션을 사용하지 않음 (JWT 방식)
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터를 Spring Security 필터 앞에 등록


        return http.build();
    }

}
