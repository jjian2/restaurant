package com.example.restaurant.service;

import org.springframework.beans.factory.annotation.Value;
import com.example.restaurant.domain.Restaurant;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Service
public class KakaoLocalService {

    @Value("${kakao.local.api.key}")
    private String kakaoApiKey;

    @PostConstruct
    public void checkKey() {
        System.out.println("🔥 카카오 키: " + kakaoApiKey);
    }

    public List<Restaurant> searchRestaurants(String keyword) {
        List<Restaurant> result = new ArrayList<>();

        // 🍽 category_group_code=FD6 : 음식점만 필터링
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" 
                     + keyword + "&category_group_code=FD6";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(response.getBody());
            JSONArray documents = json.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                JSONObject item = documents.getJSONObject(i);

                Restaurant r = new Restaurant();
                r.setPlaceName(item.optString("place_name"));
                r.setAddressName(item.optString("road_address_name"));
                r.setPhone(item.optString("phone"));
                r.setCategory(item.optString("category_group_name"));
                r.setLatitude(Double.parseDouble(item.optString("y")));
                r.setLongitude(Double.parseDouble(item.optString("x")));

                result.add(r);
            }
        }

        return result;
    }
}
