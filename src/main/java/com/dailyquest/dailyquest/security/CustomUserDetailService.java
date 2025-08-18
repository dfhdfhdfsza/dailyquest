package com.dailyquest.dailyquest.security;

import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private  final UserRepository userRepository;
    public CustomUserDetailService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    //DB에서 사용자 조회
    @Override
    public UserDetails loadUserByUsername(String loginId)throws UsernameNotFoundException{

        //사용자 조회 후 없으면 예외발생
        UserEntity user=userRepository.findByLoginId(loginId).
                orElseThrow(()->new UsernameNotFoundException(("사용자를 찾을 수 없습니다.")));

        return new CustomUserDetails(user); // UserEntity를 UserDetails로 감싸서 반환
    }

    public UserDetails loadUserByUid(long uid) throws UsernameNotFoundException{
        UserEntity user=userRepository.findByUid(uid).
                orElseThrow(()->new UsernameNotFoundException(("사용자를 찾을 수 없습니다.")));

        return new CustomUserDetails(user); // UserEntity를 UserDetails로 감싸서 반환
    }
}
