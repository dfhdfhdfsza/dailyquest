package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.dto.EmailDTO;
import com.dailyquest.dailyquest.dto.JoinDTO;
import com.dailyquest.dailyquest.dto.LoginDTO;
import com.dailyquest.dailyquest.entity.UserEntity;
import com.dailyquest.dailyquest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



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

    //아이디 찾기
    @PostMapping("/find-id")
    public String findId(@ModelAttribute EmailDTO emailDTO, RedirectAttributes redirectAttributes){
      userService.sendLoginIdByEmail(emailDTO.getEmail());
      redirectAttributes.addFlashAttribute("message", "입력하신 이메일로 아이디를 보내드렸습니다.");
      return "redirect:/";
    }
}
