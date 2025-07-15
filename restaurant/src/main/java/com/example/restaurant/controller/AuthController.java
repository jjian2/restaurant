package com.example.restaurant.controller;

import com.example.restaurant.service.UserService;
import com.example.restaurant.domain.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // 회원가입 페이지 렌더링
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";  // templates/register.html
    }

    // 회원가입 처리
    @PostMapping("/registerProcess")
    public String processRegister(@ModelAttribute Users user, Model model) {
        try {
            user.setRole("ROLE_USER"); // ✅ 기본 권한 설정 추가
            userService.registerUser(user); // 비밀번호 암호화 포함
            return "redirect:/login"; // 로그인 페이지로 이동
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "회원가입 실패: " + e.getMessage());
            return "register"; // 실패 시 다시 회원가입 폼
        }
    }

}
