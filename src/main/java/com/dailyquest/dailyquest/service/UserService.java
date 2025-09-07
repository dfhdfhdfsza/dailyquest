package com.dailyquest.dailyquest.service;

import com.dailyquest.dailyquest.common.BusinessException;
import com.dailyquest.dailyquest.dto.JoinDTO;
import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.repository.UserRepository;
import com.dailyquest.dailyquest.security.JwtTokenProvider;
import com.dailyquest.dailyquest.common.ErrorCode;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private  final  EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private  final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private  final  JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder,EmailService emailService
    ,JwtTokenProvider jwtTokenProvider,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.emailService=emailService;
        this.jwtTokenProvider=jwtTokenProvider;
        this.passwordEncoder=passwordEncoder;
    }

    @Transactional(readOnly = true)
    public boolean checkId(String id){
        return userRepository.existsByLoginId(id);
    }
    @Transactional(readOnly = true)
    public boolean checkEmail(String email){
        return userRepository.existsByEmail(email);
    }

    //회원가입 처리
    public Boolean signupProcess(JoinDTO jDTO) {
        String id=jDTO.getLoginId();
        String email=jDTO.getEmail();

        //아이디 중복체크
        if(userRepository.existsByLoginId(id)){
            throw new BusinessException(ErrorCode.USER_DUPLICATE_ID);
        }
        //이메일 중복체크
        if(userRepository.existsByEmail(email)){
            throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
        }

        UserEntity user=new UserEntity();
        user.setUsername(jDTO.getUsername());
        user.setLoginId(id);
        user.setPassword(bCryptPasswordEncoder.encode(jDTO.getPassword()));
        user.setRole("ROLE_USER");
        user.setEmail(jDTO.getEmail());

        userRepository.save(user);
        return true;
    }

    //이메일로 유저id 발송
    public void sendLoginIdByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            String loginId = user.get().getLoginId();

            String subject = "[DailyQuest] 아이디 찾기 안내";
            String body = "회원님의 아이디는 다음과 같습니다: " + loginId;
            try {
                emailService.send(email, subject, body);
            }catch (Exception e){
                throw new BusinessException(ErrorCode.MAIL_SEND_FAILED,"메일 전송에 실패했습니다.");
            }
        }

        // 이메일 유무 상관없이 동일한 메시지 출력 (보안 목적)
    }

    //본인확인 후 비밀번호 재설정 토큰 발급
    public String verifyAndIssueToken(String loginId, String email) {
        boolean matched = userRepository
                .findByLoginIdAndEmail(loginId, email)
                .isPresent();
        if (!matched) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "일치하는 사용자 정보가 없습니다.");
        }
        try {
            return jwtTokenProvider.createPasswordResetToken(loginId);
        } catch (Exception e) {
            // JWT 생성 실패 케이스를 따로 두고 싶다면 TOKEN_CREATE_FAILED 등 추가
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "토큰 생성 중 오류가 발생했습니다.");
        }
    }

    //비밀번호 재설정
    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        Claims c;
        try {
            c = jwtTokenProvider.parse(resetToken);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.JwtException e) { // 서명 위조/형식 오류 포함
            throw new BusinessException(ErrorCode.AUTH_TOKEN_INVALID);
        }

        String loginId = c.getSubject();


        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        // 비밀번호 규칙 검증 (8자+, 등) 필요 시 추가

        user.setPassword(passwordEncoder.encode(newPassword));
    }



}
