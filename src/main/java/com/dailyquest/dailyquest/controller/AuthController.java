//package com.dailyquest.dailyquest.controller;
//
//import com.dailyquest.dailyquest.security.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class AuthController {
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    JwtUtil jwtUtil;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest){
//        Authentication auth=authenticationManager.authenticate((new
//                UsernamePasswordAuthenticationToken(
//                        //사용자명과 비밀번호로 인증 시도
//                        authRequest.getUsername(),authRequest.getPassword()
//        )
//        ));
//        //인증성공 -> 토큰 발급
//        String token=jwtUtil.generateToken(authRequest.getUserName());
//        //클라이언트에게 JWT응답
//        return ResponseEntity.ok(new AuthResponse(token));
//    }
//}
