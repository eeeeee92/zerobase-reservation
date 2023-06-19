package com.zerobase.reservation.dto.shop;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ShopInfoDto {
    @Getter
    @NoArgsConstructor
    public static class Response {

        private Long id;

        private String name;

        private Double rating;

        private Double latitude;

        private Double longitude;

        @Builder
        private Response(Long id, String name, Double rating, Double latitude, Double longitude) {
            this.id = id;
            this.name = name;
            this.rating = rating;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public static Response of(ShopDto shopDto) {
            return Response.builder()
                    .id(shopDto.getId())
                    .name(shopDto.getName())
                    .rating(shopDto.getRating())
                    .latitude(shopDto.getLatitude())
                    .longitude(shopDto.getLongitude())
                    .build();
        }
    }
}
