package com.dailyquest.dailyquest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private  String secretkey;

    private  final long expirationMs=1000*60*60;    //토큰만료시간    1시간

    //토큰생성메서드
    public  String createToken(String username,String role){
        Claims claims= Jwts.claims().setSubject(username);
        claims.put("role",role);

        //토큰 생성 시간(now)과 만료시간 계산
        Date now = new Date();
        Date expiry=new Date(now.getTime()+expirationMs);

        //JWT 생성
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256,secretkey.getBytes(StandardCharsets.UTF_8))  //서명(Signature)을 추가
                .compact(); //최종 JWT문자열 반환
    }

    //사용자 이름 추출
    public String getUsername(String token){
        //JWT 문자열을 파싱해서 Payload(Claims)를 꺼낸 뒤, sub 필드인 username을 반환
        return Jwts.parser()
                .setSigningKey(secretkey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //토큰 유효성 검사
    public boolean validateToken(String token) {
        /*- 토큰을 파싱해보고 예외가 발생하지 않으면 유효한 토큰
-           만료되었거나 서명이 잘못된 경우 등은 `false` 반환 */
        try {
            Jwts.parser()
                    .setSigningKey(secretkey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
