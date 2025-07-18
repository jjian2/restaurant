package com.example.restaurant.controller;

import com.example.restaurant.service.SearchLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private SearchLogService searchLogService;

    // ✅ 로그인 페이지
    @GetMapping("/user/login")
    public String loginForm() {
        return "user/login";  // templates/user/login.html
    }

    // ✅ 맵 테스트용 페이지
    @GetMapping("/map")
    public String showMap() {
        return "testMap"; // templates/testMap.html
    }

    // ✅ 메인 페이지 - 인기 지역 정보 포함
    @GetMapping("/main")
    public String mainPage(Model model) {
        System.out.println("✅ /main 요청 들어옴");

        List<String> popularRegions = new ArrayList<>();

        try {
            if (searchLogService != null) {
                popularRegions = searchLogService.getTopRegions();
                System.out.println("✅ 인기 지역 리스트: " + popularRegions);
            } else {
                System.out.println("❌ searchLogService가 null입니다!");
            }
        } catch (Exception e) {
            System.out.println("❌ getTopRegions 실행 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("popularRegions", popularRegions);
        return "user/main";  // templates/user/main.html
    }
}
