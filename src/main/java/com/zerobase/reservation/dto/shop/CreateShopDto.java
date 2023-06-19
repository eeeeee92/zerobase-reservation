package com.zerobase.reservation.dto.shop;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


public class CreateShopDto {


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        @Email
        @NotNull
        private String email;

        @NotNull
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
