package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.common.BusinessException;
import com.dailyquest.dailyquest.common.ErrorCode;
import com.dailyquest.dailyquest.dto.*;
import com.dailyquest.dailyquest.repository.UserRepository;
import com.dailyquest.dailyquest.security.JwtTokenProvider;
import com.dailyquest.dailyquest.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import com.dailyquest.dailyquest.common.ApiResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.lang.Void;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private  final JwtTokenProvider jwtTokenProvider;

    public record TokenResponse(String accessToken) {}

    //중복 아이디 체크
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<Boolean>> checkId(@RequestParam
                                                            @NotBlank(message = "id는 필수입니다.")
                                                            @Pattern(regexp="^[A-Za-z][A-Za-z0-9_-]{7,19}$")
                                                            String id){
        boolean exists = userService.checkId(id);
        return ResponseEntity.ok(ApiResponse.data(exists));
    }

    //이메일 중복 체크
    @GetMapping("/check-email")
    public  ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam
                                                            @NotBlank(message = "email은 필수입니다.")
                                                            @Email(message = "올바른 이메일 형식이 아닙니다.") String email){
        boolean exists=userService.checkEmail(email);
        return ResponseEntity.ok(ApiResponse.data(exists));
    }


    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signupProcess(@RequestBody @Valid JoinDTO jDTO){
        userService.signupProcess(jDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success());
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
    public ResponseEntity<ApiResponse<TokenResponse>> verify(@RequestBody @Valid VerifyDTO verifyDTO) {
        String token = userService.verifyAndIssueToken(verifyDTO.getLoginId(), verifyDTO.getEmail());

        return ResponseEntity.ok(ApiResponse.data(new TokenResponse(token)));
    }

    //비밀번호 변경
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> confirm(@Valid @RequestBody PasswordResetDTO dto) {
        Claims claims = jwtTokenProvider.parse(dto.getToken());
        String purpose = claims.get("purpose", String.class);

        if (!"pwd_reset".equals(purpose)){
            throw new BusinessException(ErrorCode.AUTH_TOKEN_PURPOSE_MISMATCH);
        }
        if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
        userService.resetPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok(ApiResponse.message("비밀번호가 변경되었습니다."));
        // 필요 시: 비밀번호 정책 위반 등도 BusinessException으로 처리
    }


}
