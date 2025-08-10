package com.dailyquest.dailyquest.service;

import com.dailyquest.dailyquest.dto.JoinDTO;
import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.repository.UserRepository;
import com.dailyquest.dailyquest.security.JwtTokenProvider;
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

    //회원가입 처리
    public Boolean signupProcess(JoinDTO jDTO) {
        String id=jDTO.getLoginId();

        //아이디 중복체크
        Boolean isExist=userRepository.existsByloginId(id);
        if(isExist){
            return false;
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

            emailService.send(email, subject, body);
        }

        // 이메일 유무 상관없이 동일한 메시지 출력 (보안 목적)
    }

    public String verifyAndIssueToken(String loginId, String email) {
        userRepository.findByLoginIdAndEmail(loginId, email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보가 없습니다."));
        return jwtTokenProvider.createPasswordResetToken(loginId);
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        Claims c = jwtTokenProvider.parse(resetToken);
//        if (!"pwd_reset".equals(c.get("purpose"))) {
//            throw new IllegalArgumentException("유효하지 않은 토큰 목적입니다.");
//        }
        String loginId = c.getSubject();

        //JPA의 영속성 컨텍스트가 관리하는 엔티티는, 필드 값이 바뀌면 자동으로 UPDATE 쿼리를 실행
        UserEntity user = userRepository.findByloginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 비밀번호 규칙 검증 (8자+, 등) 필요 시 추가

        user.setPassword(passwordEncoder.encode(newPassword));
    }



}
