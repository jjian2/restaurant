package com.example.restaurant.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String region;
    private String keyword;

    @Column(name = "searched_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime searchedAt;
}
