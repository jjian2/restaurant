package com.example.restaurant.repository;

import com.example.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 주소에 keyword가 포함된 레스토랑 검색
	List<Restaurant> findByPlaceNameContainingOrAddressNameContaining(String placeKeyword, String addressKeyword);

    // 장소명과 주소명이 모두 일치하는지 확인
    boolean existsByPlaceNameAndAddressName(String placeName, String addressName);
}
