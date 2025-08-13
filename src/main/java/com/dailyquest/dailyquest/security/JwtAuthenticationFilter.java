package com.dailyquest.dailyquest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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

        if(token==null){
            filterChain.doFilter(request,response);
            return;
        }

        try {
            //access토큰 파싱/검증
            var jws=jwtTokenProvider.parse(token);
            String sub=jws.getSubject();
            long uid = Long.parseLong(sub);

            //DB에서 UserEntity를 조회하고, CustomUserDetails로 변환
            UserDetails userDetails = customUserDetailService.loadUserByUid(uid);

            //인증 객체 생성 (비밀번호는 필요 없기 때문에 null)
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            //현재 요청의 보안 컨텍스트에 사용자 인증 정보 등록
            SecurityContextHolder.getContext().setAuthentication(auth);
            //다음 필터로 요청 넘김 (없으면 컨트롤러로 감)
            filterChain.doFilter(request, response);
        }catch (io.jsonwebtoken.ExpiredJwtException ex){
            // 만료
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, "TOKEN_EXPIRED");
            return;
        } catch (io.jsonwebtoken.JwtException ex) { // 서명 불일치, 형식 오류 등
            // 서명 불일치, 형식 오류 등
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, "TOKEN_INVALID");
            return;
        }catch (NumberFormatException ex){
            // sub가 숫자가 아니면 잘못된 토큰
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, "TOKEN_INVALID");
            return;
        }
    }
    private void writeUnauthorized(HttpServletResponse res, String code) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType("application/json; charset=UTF-8");
        res.getWriter().write("{\"code\":\"" + code + "\"}");
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
