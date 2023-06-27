package com.zerobase.reservation.service.review;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.review.ReviewDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ConflictException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.review.ReviewRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.ArrivalStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;


    /**
     * 리뷰 등록
     */
    @Transactional
    public ReviewDto create(String email, String shopCode, String reservationCode, Integer rating, String content, String imageUrl) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new ArgumentException(ErrorCode.RESERVATION_NOT_FOUND, reservationCode));

        //방문 이후 리뷰를 작성할 수 있다
        if (reservation.getArrivalStatus() == ArrivalStatus.N) {
            throw new ConflictException(ErrorCode.SHOP_NOT_VISITED);
        }

        //리뷰는 예약당 한번만 작성할 수 있다
        if (reviewRepository.existsByReservation(reservation)) {
            throw new ConflictException(ErrorCode.ALREADY_EXIST_REVIEW);
        }
        Member member = memberRepository.findByEmail(email).orElseThrow(() ->
                new ArgumentException(ErrorCode.MEMBER_NOT_FOUND, email));

        Shop shop = shopRepository.findByShopCode(shopCode).orElseThrow(() ->
                new ArgumentException(ErrorCode.SHOP_NOT_FOUND, shopCode));

        Review saveReview = reviewRepository.save(getReview(rating, content, imageUrl, reservation, member, shop));

        shop.updateRating(getRatingAverage(shop));

        return ReviewDto.of(saveReview);

    }


    /**
     * 리뷰 상세조회
     */
    public ReviewDto read(String reviewCode) {
        return ReviewDto.of(reviewRepository.findByReviewCode(reviewCode)
                .orElseThrow(() -> new ArgumentException(ErrorCode.REVIEW_NOT_FOUND, reviewCode)));
    }

    private double getRatingAverage(Shop shop) {
        return reviewRepository.findAllByShop(shop)
                .stream().mapToInt(Review::getRating)
                .average().orElse(0);
    }

    private static Review getReview(Integer rating, String content, String imageUrl, Reservation reservation, Member member, Shop shop) {
        return Review.builder()
                .member(member)
                .shop(shop)
                .reservation(reservation)
                .rating(rating)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }
}
