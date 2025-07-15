package com.example.restaurant.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${google.maps.api.key}")
    private String apiKey;

    @GetMapping("/test-key")
    public String testKey() {
        System.out.println("GOOGLE MAPS KEY: " + apiKey);
        return apiKey;
    }
}
