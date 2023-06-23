package com.zerobase.reservation.domain.shop;

import com.zerobase.reservation.domain.reservation.Reservation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @Column(unique = true)
    private String shopCode;

    private String name;

    private Double latitude;

    private Double longitude;

    private Double rating;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MemberShop> memberShops = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StarRating> starRatings = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @Builder
    private Shop(String name, Double latitude, Double longitude, Double rating) {
        this.shopCode = UUID.randomUUID().toString();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;

    }

}
