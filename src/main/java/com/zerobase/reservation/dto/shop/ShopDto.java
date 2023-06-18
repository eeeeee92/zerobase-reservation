package com.zerobase.reservation.dto.shop;

import com.zerobase.reservation.domain.shop.Shop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopDto {

    private Long id;

    private String name;

    private Double rating;

    private Double latitude;

    private Double longitude;

    @Builder
    private ShopDto(Long id, String name, Double rating, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static ShopDto of(Shop shop) {
        return ShopDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .rating(shop.getRating())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .build();
    }
}
