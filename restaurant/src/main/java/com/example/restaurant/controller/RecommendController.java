package com.example.restaurant.controller;

import com.example.restaurant.service.KakaoLocalService;
import com.example.restaurant.service.NaverBlogService;
import com.example.restaurant.domain.Restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RecommendController {

    @Autowired
    private KakaoLocalService kakaoLocalService;

    @Autowired
    private NaverBlogService naverBlogService;

    // ✅ application.properties에서 키를 읽어옵니다
    @Value("${google.maps.api.key}")
    private String googleMapApiKey;

    @GetMapping("/recommend")
    public String recommend(@RequestParam("query") String query, Model model) {
        List<Restaurant> restaurants = kakaoLocalService.searchRestaurants(query);
        model.addAttribute("restaurants", restaurants);

        if (!restaurants.isEmpty()) {
            Restaurant first = restaurants.get(0);
            model.addAttribute("centerLat", first.getLatitude());
            model.addAttribute("centerLng", first.getLongitude());
        } else {
            model.addAttribute("centerLat", 37.5665); // Default: 서울
            model.addAttribute("centerLng", 126.9780);
        }

        model.addAttribute("query", query);

        // ✅ 동적으로 Google Maps API 키 전달
        model.addAttribute("googleMapApiKey", googleMapApiKey);

        return "recommend";
    }
}
