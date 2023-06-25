package com.zerobase.reservation.dto.review;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CreateReviewDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        private String email;
        private String shopCode;
        private String reservationCode;
        private Integer rating;
        private String content;
        private String imageUrl;

        @Builder
        private Request(String email, String shopCode, String reservationCode, Integer rating, String content, String imageUrl) {
            this.email = email;
            this.shopCode = shopCode;
            this.reservationCode = reservationCode;
            this.rating = rating;
            this.content = content;
            this.imageUrl = imageUrl;
        }
    }
}
