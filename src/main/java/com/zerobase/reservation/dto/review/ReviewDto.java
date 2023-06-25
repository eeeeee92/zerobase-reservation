package com.zerobase.reservation.dto.review;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewDto {

    private Member member;

    private Shop shop;

    private Reservation reservation;

    private String reviewCode;

    private Integer rating;

    private String content;

    private String imageUrl;


    @Builder
    private ReviewDto(Member member, Shop shop, Reservation reservation, String reviewCode, Integer rating, String content, String imageUrl) {
        this.member = member;
        this.shop = shop;
        this.reservation = reservation;
        this.reviewCode = reviewCode;
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public static ReviewDto of(Review review) {
        return ReviewDto.builder()
                .member(review.getMember())
                .shop(review.getShop())
                .reservation(review.getReservation())
                .reviewCode(review.getReviewCode())
                .rating(review.getRating())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .build();
    }
}
