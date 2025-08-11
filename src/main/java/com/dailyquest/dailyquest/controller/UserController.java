package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.dto.*;
import com.dailyquest.dailyquest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
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

    //비밀번호 재설정 전 본인인증
    @PostMapping("/verify")
    public String verify(@ModelAttribute VerifyDTO verifyDTO,RedirectAttributes redirectAttributes) {
        String token = userService.verifyAndIssueToken(verifyDTO.getLoginId(), verifyDTO.getEmail());

        PasswordResetDTO pdto=new PasswordResetDTO();
        pdto.setToken(token);

        redirectAttributes.addFlashAttribute("passwordResetDTO",pdto);

        return "redirect:/reset-password?token=" + token;
    }

    //비밀번호 변경
    @PostMapping("/confirm")
    public String confirm(@ModelAttribute PasswordResetDTO pdto,RedirectAttributes redirectAttributes) {
        if(!pdto.getNewPassword().equals(pdto.getNewPasswordConfirm())){
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/reset-password";
        }
        userService.resetPassword(pdto.getToken(),pdto.getNewPassword());

        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");
        return "redirect:/";
    }





}
