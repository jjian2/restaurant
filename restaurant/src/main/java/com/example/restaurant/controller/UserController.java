package com.example.restaurant.controller;

import org.springframework.ui.Model; // ✅ Spring용 Model
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

@Controller
public class UserController {

    @GetMapping("/user/login")
    public String loginForm() {
        return "user/login";  // templates/login.html
	}
    @GetMapping("/main")
    public String mainPage() {
        return "user/main";  // templates/user/main.html
    }

    @GetMapping("/admin")
    public String adminLogin() {
        return "adminLogin"; // templates/adminLogin.html
    }

    @GetMapping("/map")
    public String showMap() {
        return "testMap"; // templates/testMap.html
    }
}
