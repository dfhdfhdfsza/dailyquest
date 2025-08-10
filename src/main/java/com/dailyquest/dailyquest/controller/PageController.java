package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.dto.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {
    //회원가입 페이지 이동
    @GetMapping("/signup")
    public String signupPage(Model model){
        model.addAttribute("joinDTO", new JoinDTO());
        return "signup";
    }

    //로그인 페이지 이동
    @GetMapping("/login")
    public String loginPage(Model model){
        model.addAttribute("loginDTO",new LoginDTO());
        return "login";
    }
    //아이디 찾기 페이지 이동
    @GetMapping("/find-id")
    public  String recoverPage(Model model){
        model.addAttribute("emailDTO",new EmailDTO());
        return "find-id";
    }

    //본인인증 페이지 이동
    @GetMapping("/verify")
    public String verifyPage(Model model){
        model.addAttribute("verifyDTO",new VerifyDTO());
        return "verify";
    }

    //비밀번호 재설정 페이지 이동
    @GetMapping("reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model){
        PasswordResetDTO pdto=new PasswordResetDTO();
        pdto.setToken(token);
        model.addAttribute("passwordResetDTO",pdto);
        return "reset-password";
    }



}
