package com.dailyquest.dailyquest.service;

import com.dailyquest.dailyquest.dto.JoinDTO;
import com.dailyquest.dailyquest.dto.LoginDTO;
import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private  final  EmailService emailService;
    private  final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder,EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.emailService=emailService;
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



}
