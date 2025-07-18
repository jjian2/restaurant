package com.example.restaurant.repository;

import com.example.restaurant.domain.SearchLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SearchLogRepository extends CrudRepository<SearchLog, Long> {

	@Query(value = "SELECT s.region FROM search_log s GROUP BY s.region ORDER BY COUNT(s.region) DESC LIMIT 5", nativeQuery = true)
	List<String> findTopRegions();



}