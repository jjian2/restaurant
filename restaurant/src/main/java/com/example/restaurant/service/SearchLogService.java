package com.example.restaurant.service;

import com.example.restaurant.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;

    public List<String> getTopRegions() {
        return searchLogRepository.findTopRegions();
    }
}
