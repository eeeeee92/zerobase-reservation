package com.zerobase.reservation.dto.shop;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;



public class CreateShopDto {


    @Getter
    @NoArgsConstructor
    public static class Request {
        @Email
        @NotNull
        private String email;

        @NotNull
        private String name;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;
    }
}
