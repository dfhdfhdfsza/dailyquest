//package com.dailyquest.dailyquest.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.web.webauthn.management.UserCredentialRepository;
//
//import java.util.Collections;
//
//public class CustomUserDetailService implements UserDetailsService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    //DB에서 사용자 조회
//    @Override
//    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
//
//        User user=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(("사용자를 찾을 수 없습니다."));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//    }
//}
