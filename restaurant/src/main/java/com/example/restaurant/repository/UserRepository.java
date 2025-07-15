package com.example.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.restaurant.domain.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
}
