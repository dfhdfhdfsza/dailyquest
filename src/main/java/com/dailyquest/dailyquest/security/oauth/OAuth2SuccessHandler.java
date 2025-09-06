package com.dailyquest.dailyquest.security.oauth;

import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final SocialAuthCodeService socialAuthCodeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
    throws IOException {

        OAuth2User principal=(OAuth2User) auth.getPrincipal();
        Long userId=((Number)principal.getAttributes().get("internalUserId")).longValue();

        String code=socialAuthCodeService.issueAuthCode(userId);

        //로그인 성공 후 FE의 완료 페이지로 리다이렉트
        String feSuccess= Optional.ofNullable(req.getParameter("redirect"))
                .orElse("http://localhost:8080");
        String redirectUrl=feSuccess+"?code="+code;
        res.sendRedirect(redirectUrl);

    }

}
