package com.dailyquest.dailyquest.security.oauth;

import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req)throws OAuth2AuthenticationException{
        OAuth2User oauth2User=super.loadUser(req);
        String regId=req.getClientRegistration().getRegistrationId();   // google/kakao/naver

        OAuthUserInfo info=OAuthUserInfo.of(regId,oauth2User.getAttributes());
        //info:provider,providerId,email,name,...

        //1)  회원 찾기 or 만들기
        UserEntity user=userRepository.findByProviderAndProviderId(info.getProvider(),info.getProviderId())
                .orElseGet(()->userRepository.save(
                        UserEntity.builder()
                                .provider(info.getProvider())
                                .providerId(info.getProviderId())
                                .loginId(makeSocialLoginId(info))   //규칙적인 아이디 생성
                                .password(null)
//                                .email(info.getEmail())
                                .username(info.getName())
                                .role("ROLE_USER")
                                .build()
                ));

        //2) 스프링 시큐리티 내부 표현으로 반환(권한 포함)
        var authorities= List.of(new SimpleGrantedAuthority(user.getRole()));

        //attributes에 userId같은 키도 넣어두면 이후 핸들러에서 편함
        Map<String, Object>attrs=new HashMap<>(oauth2User.getAttributes());
        attrs.put("internalUserId",user.getUid());

        String nameKey=req.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        //nameAttributeKey 의미는 크지 않음
        return  new DefaultOAuth2User(authorities,attrs,nameKey!=null?nameKey:"sub");
    }

    private  String makeSocialLoginId(OAuthUserInfo info){
        //예: google_123456/kakao_999/email 기반 등
        return info.getProvider()+"_"+info.getProviderId();
    }
}
