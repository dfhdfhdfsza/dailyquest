package com.dailyquest.dailyquest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final  CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,CustomUserDetailService customUserDetailService){
        this.jwtTokenProvider=jwtTokenProvider;
        this.customUserDetailService=customUserDetailService;
    }


    //매 요청마다 실행되어 JWT를 검사하고 인증 정보를 설정함
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);   //토큰 추출

        if (token != null && jwtTokenProvider.validateToken(token)) {   //토큰이 존재하고 유효하면 인증처리 시작
            String username = jwtTokenProvider.getLoginId(token);
            //DB에서 UserEntity를 조회하고, CustomUserDetails로 변환
            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

            //인증 객체 생성 (비밀번호는 필요 없기 때문에 null)
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            //현재 요청의 보안 컨텍스트에 사용자 인증 정보 등록
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        //다음 필터로 요청 넘김 (없으면 컨트롤러로 감)
        filterChain.doFilter(request, response);
    }

    //HTTP 요청 헤더 중 Authorization을 확인
    //형식이 Bearer JWT토큰 이면 실제 토큰 부분만 잘라서 반환
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
