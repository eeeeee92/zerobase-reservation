package com.zerobase.reservation.dto.shop;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ShopInfoDetailDto {
    @Getter
    @NoArgsConstructor
    public static class Response {

        private String shopCode;

        private String name;

        private Double rating;

        private Double latitude;

        private Double longitude;

        @Builder
        private Response(String shopCode, String name, Double rating, Double latitude, Double longitude) {
            this.shopCode = shopCode;
            this.name = name;
            this.rating = rating;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public static Response of(ShopDto shopDto) {
            return Response.builder()
                    .shopCode(shopDto.getShopCode())
                    .name(shopDto.getName())
                    .rating(shopDto.getRating())
                    .latitude(shopDto.getLatitude())
                    .longitude(shopDto.getLongitude())
                    .build();
        }
    }
}
