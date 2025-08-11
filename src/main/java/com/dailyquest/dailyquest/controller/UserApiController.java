package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;

    //중복 아이디 체크
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkId(@RequestParam String id){
        boolean exists = userRepository.existsByloginId(id);
        return ResponseEntity.ok(exists);
    }

    //이메일 중복 체크
    @GetMapping("/check-email")
    public  ResponseEntity<Boolean> checkEmail(@RequestParam String email){
        boolean exists=userRepository.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
