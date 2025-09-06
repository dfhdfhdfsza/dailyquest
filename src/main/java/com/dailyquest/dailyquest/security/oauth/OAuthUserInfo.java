package com.dailyquest.dailyquest.security.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor(staticName = "ofRaw")
public class OAuthUserInfo {
    private final String provider;  // google/kakao/naver
    private final String providerId;
    private final String email;
    private final String name;

    public static OAuthUserInfo of(String provider, Map<String,Object>attrs){
        switch (provider){
            case "google":return fromGoogle(attrs);
            case "kakao":return fromKakao(attrs);
            case "naver":return fromNaver(attrs);
            default:throw new IllegalArgumentException("Unsupported provider:"+provider);
        }
    }

    private static OAuthUserInfo fromGoogle(Map<String,Object> a){
        String sub=(String) a.get("sub");
        String email=(String) a.get("email");
        String name=(String) a.get("name");

        return ofRaw("google",sub,email,name);
    }

    private static  OAuthUserInfo fromKakao(Map<String,Object> a){
        // kakao: { id: 123, kakao_account: { email:..., profile: { nickname, profile_image_url } } }
        String id=String.valueOf(a.get("id"));
        Map<String,Object>acc=(Map<String, Object>) a.get("kakao_account");
        String email=acc!=null?(String) acc.get("email") : null;
        Map<String,Object> prof= acc!=null?(Map<String, Object>) acc.get("profile"):null;
        String name=prof !=null?(String) prof.get("nickname"):null;

        return ofRaw("kakao",id,email,name);
    }

    private  static  OAuthUserInfo fromNaver(Map<String,Object>a){
        // naver: { resultcode:..., message:..., response: { id, email, name, profile_image } }
        Map<String,Object>r=(Map<String, Object>) a.get("response");
        String id = r != null ? (String) r.get("id") : null;
        String email = r != null ? (String) r.get("email") : null;
        String name = r != null ? (String) r.get("name") : null;

        return ofRaw("naver", id, email, name);
    }

}
