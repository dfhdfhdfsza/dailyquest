package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.common.ApiResponse;
import com.dailyquest.dailyquest.dto.LoginDTO;
import com.dailyquest.dailyquest.security.RefreshTokenRepository;
import com.dailyquest.dailyquest.repository.UserRepository;
import com.dailyquest.dailyquest.security.CustomUserDetails;
import com.dailyquest.dailyquest.security.JwtTokenProvider;
import com.dailyquest.dailyquest.security.RefreshToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private  final  UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          RefreshTokenRepository refreshTokenRepository,
                          UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository=refreshTokenRepository;
        this.userDetailsService=userDetailsService;
    }

    public record LoginResponse(String accessToken, UserSummary user) {
        public record UserSummary(Long id, String role) {}
    }
    public record TokenResponse(String accessToken) {}

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginDTO loginDTO, HttpServletResponse res) {
        try {
            //로그인 가능한지 확인 요청
            //내부적으로 UserDetailsService.loadUserByloginId()을 호출
            //실패 시 AuthenticationException이 터져 catch로 이동합니다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getLoginId(), loginDTO.getPassword()));

            //성공한 인증의 주체(principal)를 꺼내,애플리케이션에서 쓰는 CustomUserDetails로 캐스팅
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            String fp = loginDTO.getFingerprint(); // 없으면 null OK


            //토큰 생성
            String access = jwtTokenProvider.createAccessToken(user.getUserEntity());
            String refresh=jwtTokenProvider.createRefreshToken(user.getLoginId().toString(),fp,loginDTO.isAutologin());

            // 리프레시 토큰 DB에 해시로 저장
            RefreshToken row = new RefreshToken();
            row.setLoginId(user.getLoginId());
            row.setTokenHash(hash(refresh));//hash로 저장  -> 탈취 대비
            row.setFingerprint(fp);
            row.setExpiresAt(Instant.now().plus(Duration.ofDays(14)));
            refreshTokenRepository.save(row);

            // HttpOnly 쿠키로 refresh 내려주기(자바스크립트 접근 불가)
            addRefreshCookie(res, refresh,loginDTO.isAutologin());

            // 응답(액세스 토큰은 바디로, 유저 정보는 선택)
            var body = new LoginResponse(
                    access,
                    new LoginResponse.UserSummary(user.getUserEntity().getUid(), user.getRole())
            );
            return ResponseEntity.ok(ApiResponse.data(body));
        } catch (AuthenticationException e) {   //로그인 실패 시 401을 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>>refresh(@CookieValue(name="refresh_token",required=false)String refreshCookie,
                                    @RequestBody(required = false)Map<String,String>body,
                                    HttpServletResponse res){

        //리프레시 쿠키가 없으면 곧바로 401(또는 지정한 unauthorized() 응답) 반환.
        if(refreshCookie==null)return unauthorized("NO_REFRESH", "리프레시 쿠키가 없습니다.");

        try {
            //리프레시 토큰 파싱
            var jws=jwtTokenProvider.parseRefresh(refreshCookie);
            String loginId=jws.getBody().getSubject();
            String fp=(String) jws.getBody().get("fp");
            Boolean pr=jws.getBody().get("pr", Boolean.class);    // 자동로그인 여부   npe 방지
            boolean persistent = Boolean.TRUE.equals(pr);

            //DB검증
            //이미 폐기된 토큰은 거부.
            var row=refreshTokenRepository.findByTokenHashAndRevokedFalse(hash(refreshCookie))
                    .orElseThrow(()->new RuntimeException("NOT FOUND"));

            //지문 일치 확인
            if(fp!=null&&!Objects.equals(fp,row.getFingerprint())){
                throw new RuntimeException("FINGERPRINT_MISMATH");
            }
            //만료 확인
            if(row.getExpiresAt().isBefore(Instant.now())){
                throw new RuntimeException("REFRESH_EXPRIED");
            }
            //유저 권한 조회
            CustomUserDetails user=(CustomUserDetails) userDetailsService.loadUserByUsername(loginId);

            //회전 전략:이전 토큰 무효화+새 refresh 재발급
            row.setRevoked(true);
            refreshTokenRepository.save(row);

            //새 액세스 토큰과 새 리프레시 토큰 발급.
            String newAccess=jwtTokenProvider.createAccessToken(user.getUserEntity());
            String newRefresh=jwtTokenProvider.createRefreshToken(loginId,fp,persistent);

            //새 리프레시 토큰을 DB에 저장
            RefreshToken rotated=new RefreshToken();
            rotated.setLoginId(loginId);
            rotated.setTokenHash(hash(newRefresh));
            rotated.setFingerprint(fp);
            rotated.setExpiresAt(Instant.now().plus(Duration.ofDays(14)));
            refreshTokenRepository.save(rotated);

            addRefreshCookie(res,newRefresh,persistent);

            return ResponseEntity.ok(ApiResponse.data(new TokenResponse(newAccess)));
        }catch (Exception e){
            return unauthorized("REFRESH_INVALID", "리프레시 토큰이 유효하지 않습니다.");
        }
    }
    //로그아웃 시 리프레시 토큰 무효화 + 쿠키 삭제
    @PostMapping("/logout")
    public  ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails me,
                                     @CookieValue(name = "refresh_token", required = false) String refreshCookie,
                                     HttpServletResponse res){
        //현재 브라우저(디바이스)만 로그아웃
        if (refreshCookie != null) {
            try {
                refreshTokenRepository.revokeByTokenHash(hash(refreshCookie));
            } catch (Exception ignored) { /* 이미 지워진 경우 등 */ }
        }

        clearRefreshCookie(res);    //클라이언트(브라우저)에 저장된 HttpOnly 리프레시 쿠키를 삭제
        return ResponseEntity.ok(ApiResponse.message("로그아웃되었습니다."));
    }

    //persistent=true면 Max-Age(15일), false면 세션 쿠키
    private void addRefreshCookie(HttpServletResponse res, String token, boolean persistent) {
        ResponseCookie.ResponseCookieBuilder  b = ResponseCookie.from("refresh_token", token) //쿠키 빌더
                .httpOnly(true)             //httpOnly쿠키
                .secure(true)                // HTTPS 연결에서만 전송
                .sameSite("Strict")          // 사이트 간 전송 거의 차단
                .path("/api/auth");               // /auth 경로 이하의 요청에만 쿠키가 자동 전송
                if (persistent){
                    b.maxAge(Duration.ofDays(14));  // rememberMe일 때만 지속 쿠키
                }// remember=false면 Max-Age 미설정 → 세션 쿠키

        res.addHeader(HttpHeaders.SET_COOKIE, b.build().toString());   //응답 헤더에 추가
    }

    //브라우저에 저장된 refresh_token 쿠키를 삭제하는 메서드
    private void clearRefreshCookie(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(0)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    //임의의 문자열(raw)을 SHA-256으로 해시한 뒤 Base64로 인코딩해서 반환하는 메서드
    private String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    //401 Unauthorized 응답을 일정한 형식으로 만들어 보내는 작은 헬퍼
    private ResponseEntity<ApiResponse<TokenResponse>> unauthorized(String code, String msg) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(code, msg));
    }
}
