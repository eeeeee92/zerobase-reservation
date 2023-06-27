package com.zerobase.reservation.dto.shop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ShopInfoDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {

        private String shopCode;

        private String name;

        private Double distance;

        private Double rating;

        private Double latitude;

        private Double longitude;
    }
}
