package com.example.restaurant.domain;

import jakarta.persistence.*;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "address_name")
    private String addressName;

    private String phone;
    private String category;
    private Double latitude;
    private Double longitude;

    // ⭐ 추가 필드 시작
    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private int viewCount;

    private String kakaoPlaceId;  // ✅ 장소 ID
    @Transient
    private int reviewCount;      // ✅ 리뷰 수
    @Transient
    private String badge;         // ✅ 🥇🥈🥉

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users writer;
    // ⭐ 추가 필드 끝

    public Restaurant() {}

    // 기본 필드 getter/setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public String getAddressName() { return addressName; }
    public void setAddressName(String addressName) { this.addressName = addressName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public Users getWriter() { return writer; }
    public void setWriter(Users writer) { this.writer = writer; }

    // ⭐ 추가 필드 getter/setter
    public String getKakaoPlaceId() {
        return kakaoPlaceId;
    }

    public void setKakaoPlaceId(String kakaoPlaceId) {
        this.kakaoPlaceId = kakaoPlaceId;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }
}
