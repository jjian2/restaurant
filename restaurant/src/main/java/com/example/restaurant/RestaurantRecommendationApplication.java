package com.example.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.example.restaurant")
public class RestaurantRecommendationApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantRecommendationApplication.class, args);
    }
}
