package com.dailyquest.dailyquest.service;

import com.dailyquest.dailyquest.dto.JoinDTO;
import com.dailyquest.dailyquest.dto.LoginDTO;
import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private  final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }

    //회원가입 처리
    public Boolean signupProcess(JoinDTO jDTO) {
        String id=jDTO.getLoginId();

        //아이디 중복체크
        Boolean isExist=userRepository.existsById(id);
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


    public  void loginProcess(LoginDTO ldto){

        String id=ldto.getLoginId();
        String password=ldto.getPassword();

        Boolean isExist=userRepository.existsById(id);

        if(isExist){
            return;
        }

        UserEntity data=new UserEntity();
        data.setUsername(id);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");
        data.setEmail("abc@naver.com");
        userRepository.save(data);
    }
}
