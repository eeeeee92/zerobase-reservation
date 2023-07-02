package com.zerobase.reservation.dto.review;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReviewInfoDto {
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Response {

        private String email;
        private String nickname;
        private String memberImageUrl;
        private String shopCode;
        private String shopName;
        private String reviewCode;
        private Integer rating;
        private String content;
        private String reviewImageUrl;

        @Builder
        private Response(String email, String nickname, String memberImageUrl, String shopCode, String shopName, String reviewCode, Integer rating, String content, String reviewImageUrl) {
            this.email = email;
            this.nickname = nickname;
            this.memberImageUrl = memberImageUrl;
            this.shopCode = shopCode;
            this.shopName = shopName;
            this.reviewCode = reviewCode;
            this.rating = rating;
            this.content = content;
            this.reviewImageUrl = reviewImageUrl;
        }

        public static Response of(ReviewDto reviewDto) {
            return Response.builder()
                    .email(reviewDto.getMember().getEmail())
                    .nickname(reviewDto.getMember().getNickname())
                    .memberImageUrl(reviewDto.getMember().getImageUrl())
                    .shopCode(reviewDto.getShop().getShopCode())
                    .shopName(reviewDto.getShop().getName())
                    .reviewCode(reviewDto.getReviewCode())
                    .rating(reviewDto.getRating())
                    .content(reviewDto.getContent())
                    .reviewImageUrl(reviewDto.getImageUrl())
                    .build();
        }
    }
}
