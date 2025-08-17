package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.common.BusinessException;
import com.dailyquest.dailyquest.dto.*;
import com.dailyquest.dailyquest.repository.UserRepository;
import com.dailyquest.dailyquest.service.UserService;
import jakarta.validation.Valid;
import com.dailyquest.dailyquest.common.ApiResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.lang.Void;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    //중복 아이디 체크
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<Boolean>> checkId(@RequestParam @NotBlank(message = "id는 필수입니다.") String id){
        boolean exists = userRepository.existsByloginId(id);
        return ResponseEntity.ok(ApiResponse.data(exists));
    }

    //이메일 중복 체크
    @GetMapping("/check-email")
    public  ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam @NotBlank(message = "email은 필수입니다.")
                                                            @Email(message = "올바른 이메일 형식이 아닙니다.") String email){
        boolean exists=userRepository.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.data(exists));
    }


    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signupProcess(@RequestBody @Valid JoinDTO jDTO){
        boolean isexist=userService.signupProcess(jDTO);

        if(!isexist){   //아이디 중복등 비즈니스 로직 실패
            throw new BusinessException("DUPLICATE_USER", "이미 사용 중인 아이디/이메일입니다.");
        }

        // Location 헤더를 넣을 수도 있음: URI location = URI.create("/api/users/" + dto.getLoginId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success());
    }

    //아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse<Void>> findId(@RequestBody @Valid EmailDTO emailDTO){
      userService.sendLoginIdByEmail(emailDTO.getEmail());
      // 처리 완료 후 바디 없이 204도 가능
        return ResponseEntity.accepted().body(ApiResponse.message("입력하신 이메일로 아이디를 전송했습니다."));
    }

    //비밀번호 재설정 전 본인인증
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestBody @Valid VerifyDTO verifyDTO) {
        String token = userService.verifyAndIssueToken(verifyDTO.getLoginId(), verifyDTO.getEmail());

        // 보안 관점에서 토큰은 "바디로 직접 반환하지 않거나" 최소한 1회용·짧은 TTL로 발급 권장.
        // 여기서는 예시로 바디에 내려주고, 프론트가 reset-password 화면으로 라우팅.
        return ResponseEntity.ok(ApiResponse.data(new String(token)));
    }

    //비밀번호 변경
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> confirm(@Valid @RequestBody PasswordResetDTO dto) {
        if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
            throw new BusinessException("PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
        }
        userService.resetPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok(ApiResponse.message("비밀번호가 변경되었습니다."));
        // 필요 시: 비밀번호 정책 위반 등도 BusinessException으로 처리
    }





}
