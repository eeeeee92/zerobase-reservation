package com.zerobase.reservation.dto.shop;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UpdateShopDto {


    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {
        private String email;
        private String name;
        private Double latitude;
        private Double longitude;

        @Builder
        private Request(String email, String name, Double latitude, Double longitude) {
            this.email = email;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
