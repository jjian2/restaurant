package com.example.restaurant.service;

import com.example.restaurant.domain.Restaurant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
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

    // ✅ 음식점 키워드 검색 + 리뷰 수 크롤링 및 정렬
    public List<Restaurant> searchRestaurants(String keyword) {
        List<Restaurant> result = new ArrayList<>();

        String adjustedKeyword = keyword + " 맛집";
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + adjustedKeyword + "&category_group_code=FD6";

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

                // ✅ place_url에서 ID 추출 (숫자만)
                String placeUrl = item.optString("place_url");
                String kakaoPlaceId = placeUrl.replaceAll("\\D+", ""); // 숫자만 추출
                r.setKakaoPlaceId(kakaoPlaceId);

                // ✅ 리뷰 수 크롤링 및 로그 출력
                System.out.println("📡 리뷰 크롤링 URL: https://place.map.kakao.com/" + kakaoPlaceId);
                int reviewCount = getKakaoReviewCount(kakaoPlaceId);
                r.setReviewCount(reviewCount);

                result.add(r);
            }
        }

        // 리뷰 수 내림차순 정렬
        result.sort((r1, r2) -> Integer.compare(r2.getReviewCount(), r1.getReviewCount()));

        // 🥇🥈🥉 뱃지 부여
        for (int i = 0; i < result.size(); i++) {
            if (i == 0) result.get(i).setBadge("🥇");
            else if (i == 1) result.get(i).setBadge("🥈");
            else if (i == 2) result.get(i).setBadge("🥉");
            else result.get(i).setBadge("");
        }

        return result;
    }

    // ✅ 주소 → 위도/경도 변환
    public double[] getCoordinatesByAddress(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject json = new JSONObject(response.getBody());
            JSONArray documents = json.getJSONArray("documents");

            if (!documents.isEmpty()) {
                JSONObject item = documents.getJSONObject(0);
                double latitude = item.getDouble("y");
                double longitude = item.getDouble("x");
                return new double[]{latitude, longitude};
            }
        }

        return new double[]{0.0, 0.0};
    }

    // ✅ 카카오 장소 페이지 HTML 크롤링
    public int getKakaoReviewCount(String kakaoPlaceId) {
        try {
            String apiUrl = "https://place.map.kakao.com/main/v/" + kakaoPlaceId;
            System.out.println("📡 리뷰 JSON URL: " + apiUrl);

            Document doc = Jsoup.connect(apiUrl)
                                .ignoreContentType(true)
                                .userAgent("Mozilla/5.0")
                                .get();

            String json = doc.body().text();
            JSONObject obj = new JSONObject(json);

            if (obj.has("basicInfo")) {
                JSONObject basic = obj.getJSONObject("basicInfo");
                return basic.optInt("reviewCount", 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
