//package com.dailyquest.dailyquest.security;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//import java.util.Date;
//
//public class JwtUtil {
//    private final String SECRET_KEY = "secret_key"; // 🔐 JWT 서명에 사용할 비밀키
//
//    // 📌 JWT 토큰 생성 메서드
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)                                 // JWT에 저장할 사용자 이름
//                .setIssuedAt(new Date())                              // 생성일자
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 만료시간 (1시간)
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)      // 서명 알고리즘 + 키
//                .compact();                                           // 최종 토큰 문자열 생성
//    }
//
//    // 📌 JWT에서 사용자 이름(username) 추출
//    public String extractUsername(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET_KEY)                          // 검증용 키 설정
//                .parseClaimsJws(token)                              // 토큰 파싱
//                .getBody()
//                .getSubject();                                      // subject 값 꺼냄
//    }
//
//    // 📌 토큰 유효성 검사
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token); // 파싱 성공 = 유효
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//}
