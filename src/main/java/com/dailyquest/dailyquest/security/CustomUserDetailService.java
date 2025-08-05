package com.dailyquest.dailyquest.security;

import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class CustomUserDetailService implements UserDetailsService {

    private  final UserRepository userRepository;
    public CustomUserDetailService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    //DB에서 사용자 조회
    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{

        //사용자 조회 후 없으면 예외발생
        UserEntity user=userRepository.findByUsername(username).
                orElseThrow(()->new UsernameNotFoundException(("사용자를 찾을 수 없습니다.")));

        return new CustomUserDetails(user); // 커스텀 UserDetails 반환
    }
}
