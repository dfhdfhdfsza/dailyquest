package com.dailyquest.dailyquest.security;

import com.dailyquest.dailyquest.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private  String secretkey;
    @Value("${jwt.refresh.secret}")
    private String refreshSecretKey;

    private SecretKey key;
    private SecretKey refreshKey;

    @Value("${jwt.access-ttl}")  private Duration ttl;    //토큰 만료시간 15분
    @Value("${jwt.refresh-ttl}") private Duration refreshTtl;   //14일


    //secretkey가 Spring으로부터 주입된 이후에 key를 초기화
    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secretkey.getBytes(StandardCharsets.UTF_8));
        refreshKey=Keys.hmacShaKeyFor(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    //토큰 생성 메서드
    public  String createAccessToken(UserEntity user){
        Instant now=Instant.now();

        //JWT 생성
        return Jwts.builder()
                .setSubject(Long.toString(user.getUid()))    //사용자 ID 설정
                .claim("loginId",user.getLoginId())
                .claim("role",user.getRole()) //role 설정
                .setIssuedAt(Date.from(now))    //토큰 발급시간
                .setExpiration(Date.from(now.plus(ttl)))    //토큰 만료시간
                .signWith(key,SignatureAlgorithm.HS256)  //서명(Signature)을 추가
                .compact(); //최종 JWT문자열 반환
    }

    //리프레시 토큰 생성
    public String createRefreshToken(String loginId,String fingerprint,boolean persistent){
        Instant now=Instant.now();

        return Jwts.builder()
                .setSubject(loginId)
                .claim("fp",fingerprint)
                .claim("pr", persistent)             // 자동로그인 여부
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(Instant.now().plus(refreshTtl)))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();

    }

//    //사용자 이름 추출
//    public String getUid(String token){
//        //JWT 문자열을 파싱해서 Payload(Claims)를 꺼낸 뒤, sub 필드인 username을 반환
//        return Jwts.parserBuilder()
//                .setSigningKey(key)     //토큰 검증할때 사용할 시크릿키 설정
//                .build()       //파서 빌더완성
//                .parseClaimsJws(token)  //JWT를 실제로 파싱 및 검증 수행
//                .getBody()
//                .getSubject();          //JWT 내부의 subject 추출
//
//    }
//
//    //토큰 유효성 검사
//    public boolean validateToken(String token) {
//        /* 토큰을 파싱해보고 예외가 발생하지 않으면 유효한 토큰
//           만료되었거나 서명이 잘못된 경우 등은 false 반환 */
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    //로그인 ID 기반으로 만료시간이 포함된 JWT 토큰을 생성
    public String createPasswordResetToken(String loginId){
        Instant now=Instant.now();

        return Jwts.builder()
                .setSubject(loginId)
                .setExpiration(Date.from(now.plus(ttl)))
                .claim("purpose","pwd_reset")
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();
    }

    //JWT 토큰을 검증하고 내부에 들어 있는 정보(Claims)를 꺼내는 역할
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }
    public Jws<Claims> parseRefresh(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey).build()
                .parseClaimsJws(token);
    }



}
