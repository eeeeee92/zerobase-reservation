package com.zerobase.reservation.dto.review;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

public class CreateReviewDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        @Email
        @NotNull
        private String email;

        @NotEmpty
        private String shopCode;

        @NotEmpty
        private String reservationCode;

        @NotNull
        @Positive
        @Min(value = 1)
        @Max(value = 5)
        private Integer rating;

        @NotEmpty
        @Length(max = 500)
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
