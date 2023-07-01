package com.zerobase.reservation.dto.shop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DeleteShopDto {

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {
        String email;
    }
}
