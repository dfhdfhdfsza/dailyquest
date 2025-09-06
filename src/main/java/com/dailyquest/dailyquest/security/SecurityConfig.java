package com.dailyquest.dailyquest.security;

import com.dailyquest.dailyquest.config.CorsProps;
import com.dailyquest.dailyquest.repository.UserRepository;
import com.dailyquest.dailyquest.security.limit.LoginEndpointRateLimitFilter;
import com.dailyquest.dailyquest.security.oauth.CustomOAuth2UserService;
import com.dailyquest.dailyquest.security.oauth.OAuth2FailureHandler;
import com.dailyquest.dailyquest.security.oauth.OAuth2SuccessHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


import java.time.Duration;
import java.util.List;

@Configuration
@EnableWebSecurity  //Spring Security 설정을 사용자 정의로 오버라이드
@EnableConfigurationProperties(CorsProps.class)
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService userDetailService;
    private  final  CorsProps corsProps;
    private final LoginEndpointRateLimitFilter loginRateLimitFilter;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    public  SecurityConfig(JwtTokenProvider jwtTokenProvider,CustomUserDetailService userDetailService,
                           UserRepository userRepository,CorsProps corsProps,LoginEndpointRateLimitFilter loginRateLimitFilter,
                           CustomOAuth2UserService customOAuth2UserService,OAuth2SuccessHandler oAuth2SuccessHandler,
                           OAuth2FailureHandler oAuth2FailureHandler){
        this.jwtTokenProvider=jwtTokenProvider;
        this.userDetailService=userDetailService;
        this.corsProps=corsProps;
        this.loginRateLimitFilter=loginRateLimitFilter;
        this.customOAuth2UserService=customOAuth2UserService;
        this.oAuth2SuccessHandler=oAuth2SuccessHandler;
        this.oAuth2FailureHandler=oAuth2FailureHandler;
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {  //password 암호화 설정

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationFilter jwtFilter=new JwtAuthenticationFilter(jwtTokenProvider,userDetailService);

        http.csrf(csrf -> csrf.disable())   //CSRF(사이트 간 요청 위조) 방지 기능 비활성화
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                // 필요할 때만 세션 생성 (OAuth2에 필요)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                //SecurityContext는 명시 저장일 때만 세션에 보관 (JWT 경로에서 세션 생기는 것 방지)
                .securityContext(sc -> sc.requireExplicitSave(true))
                .exceptionHandling(e -> e
                        // 인증 없을 때 403 말고 401 + JSON 주도록
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(HttpStatus.UNAUTHORIZED.value());
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"code\":\"UNAUTHORIZED\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth   //http 요청에 대한 접근제어 설정
                        .requestMatchers(                       //swagger 관련은 인증없이 접근 허용
                        "/swagger-ui/**","/swagger-resources/**",
                        "/v3/api-docs/**","/css/**","/js/**","/error", "/error/**","/fontawesome/**","/health",
                                "/oauth2/**","/login/oauth2/**","/api/auth/social/exchange")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,"/","/login", "/signup","/join","/find-id",
                                "/verify","/reset-password","api/users/check-id", "api/users/check-email")//인덱스,로그인,회원가입
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users/**","/api/auth/**")       //아이디찾기,본인인증,비밀번호찾기
                        .permitAll()
                        .anyRequest().authenticated()       //그 외 요청은 로그인 필요
                )
                // JWT 필터를 Spring Security 필터 앞에 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginRateLimitFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

                .oauth2Login(oauth->oauth
                        .userInfoEndpoint(u->u.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .logout(l->l.logoutUrl("/api/auth/logout").logoutSuccessHandler((req,res,auth)->
                {
                    var cookie=new Cookie("refresh_token","");
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    res.addCookie(cookie);
                    res.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }));


        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(corsProps.getAllowedOrigins());
        cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("Content-Type","Authorization","X-Requested-With"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(java.time.Duration.ofHours(1));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

}
