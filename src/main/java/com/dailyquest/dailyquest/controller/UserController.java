package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.dto.JoinDTO;
import com.dailyquest.dailyquest.dto.LoginDTO;
import com.dailyquest.dailyquest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //로그인
    @PostMapping("/login")
    public String loginProcess(LoginDTO ldto){
        userService.loginProcess(ldto);

        return  "ok";
    }

    //회원가입
    @PostMapping("/signup")
    public String signupProcess(JoinDTO jDTO){
        boolean isexist=userService.signupProcess(jDTO);

        if(!isexist){
            return "false";
        }

        return "redirect:/";
    }
}
