package com.dailyquest.dailyquest.controller;

import com.dailyquest.dailyquest.dto.JoinDTO;
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


}
