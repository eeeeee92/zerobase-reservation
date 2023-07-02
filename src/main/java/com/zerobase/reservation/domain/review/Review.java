package com.zerobase.reservation.domain.review;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(unique = true, nullable = false)
    private String reviewCode;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private String content;

    private String imageUrl;


    @Builder
    private Review(Member member, Shop shop, Reservation reservation, Integer rating, String content, String imageUrl) {
        this.member = member;
        this.shop = shop;
        this.reservation = reservation;
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
        this.reviewCode = UUID.randomUUID().toString();
    }

    public void updateReview(Integer rating, String content, String imageUrl) {
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
    }

}
