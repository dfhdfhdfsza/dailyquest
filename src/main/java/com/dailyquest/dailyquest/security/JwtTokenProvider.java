package com.dailyquest.dailyquest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}") // application.yml에 정의한 시크릿 키를 주입
    private  String secretkey;
    private SecretKey key;
    private  final long expirationMs=1000*60*60;    //토큰만료시간    1시간

    //secretkey가 Spring으로부터 주입된 이후에 key를 초기화
    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secretkey.getBytes(StandardCharsets.UTF_8));
    }

    //토큰생성메서드
    public  String createToken(String username,String role){
        Claims claims= Jwts.claims().setSubject(username);  //사용자 ID 설정
        claims.put("role",role);                            //role 설정

        //토큰 생성 시간(now)과 만료시간 계산
        Date now = new Date();
        Date expiry=new Date(now.getTime()+expirationMs);

        //JWT 생성
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key,SignatureAlgorithm.HS256)  //서명(Signature)을 추가
                .compact(); //최종 JWT문자열 반환
    }

    //사용자 이름 추출
    public String getUsername(String token){
        //JWT 문자열을 파싱해서 Payload(Claims)를 꺼낸 뒤, sub 필드인 username을 반환
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

    }

    //토큰 유효성 검사
    public boolean validateToken(String token) {
        /*- 토큰을 파싱해보고 예외가 발생하지 않으면 유효한 토큰
-           만료되었거나 서명이 잘못된 경우 등은 `false` 반환 */
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
