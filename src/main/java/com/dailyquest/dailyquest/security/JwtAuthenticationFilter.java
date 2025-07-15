//package com.dailyquest.dailyquest.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private UserDetailsService userDetailsService; // 📌 사용자 정보 가져오기 위한 서비스
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization"); // 📌 요청 헤더에서 Authorization 꺼냄
//
//        // 📌 "Bearer "로 시작하면 JWT 토큰임
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7); // "Bearer " 제거하고 토큰만 추출
//            String username = jwtUtil.extractUsername(token); // JWT에서 username 꺼냄
//
//            // ✅ 현재 SecurityContext에 인증 정보가 없다면 처리
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // DB에서 사용자 정보 조회
//
//                // ✅ 토큰이 유효하면 인증 처리
//                if (jwtUtil.validateToken(token)) {
//                    UsernamePasswordAuthenticationToken authToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                    SecurityContextHolder.getContext().setAuthentication(authToken); // Spring Security에 사용자 등록
//                }
//            }
//        }
//        filterChain.doFilter(request, response); // 다음 필터로 넘김
//    }
//}
