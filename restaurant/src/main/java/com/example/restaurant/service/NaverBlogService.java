package com.example.restaurant.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.util.*;

@Service
public class NaverBlogService {

    private final String CLIENT_ID = "54MOgLrs08iAfvdTkmh0";
    private final String CLIENT_SECRET = "PmVcNh2neX";

    // 🔹 이름 + 주소 기반 필터링 분석 메서드
    public Map<String, Object> analyzeBlogReviews(String name, String address) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> reviews = new ArrayList<>();
        List<String> allText = new ArrayList<>();

        int posCount = 0, negCount = 0;
        String[] posWords = {"맛있", "훌륭", "추천", "친절", "좋", "감동"};
        String[] negWords = {"별로", "실망", "불친절", "나쁘", "최악"};

        try {
            String query = name + " " + address;
            URL url = new URL("https://openapi.naver.com/v1/search/blog?query=" + URLEncoder.encode(query, "UTF-8"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            JSONObject json = new JSONObject(sb.toString());
            JSONArray items = json.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String title = item.getString("title").replaceAll("<.*?>", "");
                String desc = item.getString("description").replaceAll("<.*?>", "");
                String link = item.getString("link");

                // 🔐 필터링: 가게 이름/주소가 제목 또는 내용에 없으면 건너뛰기
                if (!title.contains(name) && !desc.contains(name) &&
                    !title.contains(address) && !desc.contains(address)) {
                    continue;
                }

                Map<String, String> review = new HashMap<>();
                review.put("title", title);
                review.put("desc", desc);
                review.put("link", link);
                reviews.add(review);
                allText.add(desc);

                for (String word : posWords) {
                    if (desc.contains(word)) posCount++;
                }
                for (String word : negWords) {
                    if (desc.contains(word)) negCount++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🔸 키워드 빈도 분석
        Map<String, Integer> freqMap = new HashMap<>();
        for (String text : allText) {
            for (String word : text.replaceAll("[^가-힣 ]", "").split("\\s+")) {
                if (word.length() < 2) continue;
                freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
            }
        }

        // 🔸 상위 키워드 정렬
        List<Map.Entry<String, Integer>> topWords = new ArrayList<>(freqMap.entrySet());
        topWords.sort((a, b) -> b.getValue() - a.getValue());

        // 🔸 결과 저장
        result.put("reviews", reviews);
        result.put("posCount", posCount);
        result.put("negCount", negCount);
        result.put("topWords", topWords.subList(0, Math.min(50, topWords.size())));
        return result;
    }
}
