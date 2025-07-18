package com.example.restaurant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.restaurant.service.NaverBlogService;

import java.util.Map;

@Controller
public class AnalyzeController {

    @Autowired
    private NaverBlogService naverBlogService;

    @GetMapping("/recommend/analyze")
    public String analyzeRestaurant(@RequestParam("name") String name,
                                    @RequestParam(value = "address", required = false) String address,
                                    Model model) {
        try {
            // 📌 정확한 검색을 위해 name + address 전달
            Map<String, Object> result = naverBlogService.analyzeBlogReviews(name, address);

            model.addAttribute("placeName", name);
            model.addAttribute("reviews", result.get("reviews"));
            model.addAttribute("posCount", result.get("posCount"));
            model.addAttribute("negCount", result.get("negCount"));
            model.addAttribute("topWords", result.get("topWords"));

            return "analyze/analyze";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";  // 에러 처리 페이지로 연결
        }
    }
}
