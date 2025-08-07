package com.dailyquest.dailyquest.security;

import com.dailyquest.dailyquest.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  //Spring Security 설정을 사용자 정의로 오버라이드
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService userDetailService;
    private final UserRepository userRepository;

    public  SecurityConfig(JwtTokenProvider jwtTokenProvider,CustomUserDetailService userDetailService,
                           UserRepository userRepository){
        this.jwtTokenProvider=jwtTokenProvider;
        this.userDetailService=userDetailService;
        this.userRepository=userRepository;
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {  //password 암호화 설정

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtFilter=new JwtAuthenticationFilter(jwtTokenProvider,userDetailService);

        http.csrf(csrf -> csrf.disable())   //CSRF(사이트 간 요청 위조) 방지 기능 비활성화
                .authorizeHttpRequests(auth -> auth   //http 요청에 대한 접근제어 설정
                        .requestMatchers(                       //swagger 관련은 인증없이 접근 허용
                                "/swagger-ui/**","/swagger-resources/**",
                                "/v3/api-docs/**","/css/**","/api/**","/js/**",
                                "/","/login", "/signup","/join","recover","/find-id"         //인덱스,로그인,회원가입,아이디/비번찾기
                        ).permitAll()
                        .anyRequest().authenticated()       //그 외 요청은 로그인 필요
                )
                // 세션을 사용하지 않음 (JWT 방식)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JWT 필터를 Spring Security 필터 앞에 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
