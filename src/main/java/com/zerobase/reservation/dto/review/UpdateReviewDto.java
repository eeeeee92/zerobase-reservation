package com.zerobase.reservation.dto.review;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UpdateReviewDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        private String email;
        private String content;
        private Integer rating;
        private String imageUrl;

        @Builder
        private Request(String email, String content, Integer rating, String imageUrl) {
            this.email = email;
            this.content = content;
            this.rating = rating;
            this.imageUrl = imageUrl;
        }
    }
}
