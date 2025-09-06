package com.dailyquest.dailyquest.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure( HttpServletRequest req,HttpServletResponse res, AuthenticationException ex)
    throws IOException, ServletException {

        String feFail= Optional.ofNullable(req.getParameter("redirect"))
                .orElse("http://localhost:8080/oauth2/fail");
        res.sendRedirect(feFail+"?error="+ URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8));
    }

}
