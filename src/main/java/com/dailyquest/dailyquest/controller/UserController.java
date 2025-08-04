package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.dto.JoinDTO;
import com.dailyquest.dailyquest.dto.LoginDTO;
import com.dailyquest.dailyquest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String signupProcess(@ModelAttribute("joinDTO") @Valid JoinDTO jDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){  //유효성 검사 실패 시
            return "signup";
        }
        boolean isexist=userService.signupProcess(jDTO);

        if(!isexist){   //아이디 중복등 비즈니스 로직 실패
            return "false";
        }

        return "redirect:/";
    }
}
