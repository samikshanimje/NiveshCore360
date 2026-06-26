package com.niveshcore360.controller;

import com.niveshcore360.entity.User;
import com.niveshcore360.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user) {

        user.setRole("USER");

        userService.save(user);

        return "redirect:/login";
    }

}