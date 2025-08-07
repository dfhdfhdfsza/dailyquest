package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.dto.EmailDTO;
import com.dailyquest.dailyquest.dto.JoinDTO;
import com.dailyquest.dailyquest.dto.LoginDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/signup")
    public String signupPage(Model model){
        model.addAttribute("joinDTO", new JoinDTO());
        return "signup";
    }

    @GetMapping("/login")
    public String loginPage(Model model){
        model.addAttribute("loginDTO",new LoginDTO());
        return "login";
    }

    @GetMapping("/recover")
    public  String recoverPage(Model model){
        model.addAttribute("emailDTO",new EmailDTO());
        return "recover";
    }



}
