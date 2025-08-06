package com.dailyquest.dailyquest.security;

import com.dailyquest.dailyquest.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final UserEntity user;

    public CustomUserDetails(UserEntity user){
        this.user=user;
    }

    @Override   //권한 반환
    public Collection<? extends GrantedAuthority>getAuthorities(){
        //SimpleGrantedAuthority는 Spring Security가 이해할 수 있는 권한 포맷
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername(){
        return user.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true; // 예시: 잠김 아님
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 예시: 비밀번호 만료 아님
    }

    @Override
    public boolean isEnabled() {
        return true; // 예시: 활성 계정
    }

    // 유저 정보 접근자
    public UserEntity getUserEntity() {
        return user;
    }

}
