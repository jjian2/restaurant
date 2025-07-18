package com.example.restaurant.controller;

import com.example.restaurant.domain.Restaurant;
import com.example.restaurant.domain.SearchLog;
import com.example.restaurant.domain.Users;
import com.example.restaurant.repository.RestaurantRepository;
import com.example.restaurant.repository.SearchLogRepository;
import com.example.restaurant.service.KakaoLocalService;
import com.example.restaurant.service.NaverBlogService;
import com.example.restaurant.service.SearchLogService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Controller
@RequestMapping("/recommend") 
public class RecommendController {

    @Autowired
    private KakaoLocalService kakaoLocalService;

    @Autowired
    private NaverBlogService naverBlogService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private SearchLogService searchLogService;

    @Autowired
    private SearchLogRepository searchLogRepository;

    @Value("${google.maps.api.key}")
    private String googleMapApiKey;

    // ✅ 추천 검색 결과 보기
    @GetMapping("/recommend")
    public String recommend(@RequestParam("query") String query, Model model) {
        List<Restaurant> restaurants = kakaoLocalService.searchRestaurants(query);
        model.addAttribute("restaurants", restaurants);

        if (!restaurants.isEmpty()) {
            Restaurant first = restaurants.get(0);
            model.addAttribute("centerLat", first.getLatitude());
            model.addAttribute("centerLng", first.getLongitude());
        } else {
            model.addAttribute("centerLat", 37.5665); // 서울 시청
            model.addAttribute("centerLng", 126.9780);
        }

        model.addAttribute("query", query);
        model.addAttribute("googleMapApiKey", googleMapApiKey);
        return "recommend/recommend";
    }

    // ✅ 게시글 목록 보기
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("restaurants", restaurantRepository.findAll());
        return "recommend/list";
    }

    // ✅ 게시글 작성 폼
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "recommend/write";
    }

    // ✅ 게시글 작성 처리
    @PostMapping("/write")
    public String writeSubmit(@ModelAttribute Restaurant restaurant,
                              @AuthenticationPrincipal Users user) {
        if (user == null) {
            return "redirect:/login";
        }

        restaurant.setWriter(user);
        restaurant.setViewCount(0);

        if (restaurant.getLatitude() == null || restaurant.getLongitude() == null ||
                (restaurant.getLatitude() == 0.0 && restaurant.getLongitude() == 0.0)) {
            double[] coords = kakaoLocalService.getCoordinatesByAddress(restaurant.getAddressName());
            restaurant.setLatitude(coords[0]);
            restaurant.setLongitude(coords[1]);
        }

        restaurantRepository.save(restaurant);
        return "redirect:/recommend/list";
    }

    // ✅ 게시글 상세 보기
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Restaurant> opt = restaurantRepository.findById(id);
        if (opt.isPresent()) {
            Restaurant restaurant = opt.get();
            restaurant.setViewCount(restaurant.getViewCount() + 1);
            restaurantRepository.save(restaurant);
            model.addAttribute("restaurant", restaurant);
            return "recommend/detail";
        }
        return "redirect:/recommend/list";
    }

    // ✅ 게시글 수정 폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow();
        model.addAttribute("restaurant", restaurant);
        return "recommend/edit";
    }

    // ✅ 게시글 수정 처리
    @PostMapping("/edit/{id}")
    public String editSubmit(@PathVariable Long id,
                             @ModelAttribute Restaurant updatedRestaurant,
                             @AuthenticationPrincipal Users user) {
        Restaurant existing = restaurantRepository.findById(id).orElseThrow();

        existing.setPlaceName(updatedRestaurant.getPlaceName());
        existing.setAddressName(updatedRestaurant.getAddressName());
        existing.setPhone(updatedRestaurant.getPhone());
        existing.setCategory(updatedRestaurant.getCategory());
        existing.setLatitude(updatedRestaurant.getLatitude());
        existing.setLongitude(updatedRestaurant.getLongitude());
        existing.setDescription(updatedRestaurant.getDescription());
        existing.setImageUrl(updatedRestaurant.getImageUrl());
        existing.setWriter(user);

        if ((existing.getLatitude() == null || existing.getLongitude() == null) ||
                (existing.getLatitude() == 0.0 && existing.getLongitude() == 0.0)) {
            double[] coords = kakaoLocalService.getCoordinatesByAddress(existing.getAddressName());
            existing.setLatitude(coords[0]);
            existing.setLongitude(coords[1]);
        }

        restaurantRepository.save(existing);
        return "redirect:/recommend/detail/" + id;
    }

    // ✅ 게시글 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        restaurantRepository.deleteById(id);
        return "redirect:/recommend/list";
    }

    // ✅ 🔍 검색 처리 (POST)
    @PostMapping("/recommend/search")
    public String search(@RequestParam("keyword") String keyword) {
        String region = extractRegion(keyword);
        System.out.println("🔍 검색어: " + keyword);
        System.out.println("📍 추출된 지역: " + region);

        SearchLog log = new SearchLog();
        log.setKeyword(keyword);
        log.setRegion(region);
        log.setSearchedAt(LocalDateTime.now());
        searchLogRepository.save(log);

        // ✅ 인코딩
        String encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8);
        return "redirect:/recommend?query=" + encodedRegion;
    }


    // ✅ 🔍 인기 지역 버튼 클릭 처리 (GET)
    @GetMapping("/search")
    public String searchByGet(@RequestParam("keyword") String keyword) {
        String region = extractRegion(keyword);
        SearchLog log = new SearchLog();
        log.setKeyword(keyword);
        log.setRegion(region);
        log.setSearchedAt(LocalDateTime.now());
        searchLogRepository.save(log);

        String encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8);
        return "redirect:/recommend/recommend?query=" + encodedRegion;
    }

    // ✅ 지역명 추출
    private String extractRegion(String keyword) {
        List<String> regions = List.of("강남", "홍대", "건대", "혜화", "성수", "이태원");
        return regions.stream()
                .filter(keyword::contains)
                .findFirst()
                .orElse(keyword);
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ RecommendController 로딩됨");
    }
}
