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
        Reservation reservation = getReservationBy(reservationCode);

        //방문 이후 리뷰를 작성할 수 있다
        isVisited(reservation.getArrivalStatus());

        //리뷰는 예약당 한번만 작성할 수 있다
        isReviewExist(reservation);

        Member member = getMemberBy(email);

        Shop shop = getShopBy(shopCode);

        return ReviewDto.of(reviewRepository.save(getReview(rating, content, imageUrl, reservation, member, shop)));

    }


    /**
     * 리뷰 상세조회
     */
    public ReviewDto getReview(String reviewCode) {
        return ReviewDto.of(reviewRepository.findByReviewCode(reviewCode)
                .orElseThrow(() -> new ArgumentException(ErrorCode.REVIEW_NOT_FOUND, reviewCode)));
    }


    private Reservation getReservationBy(String reservationCode) {
        return reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new ArgumentException(ErrorCode.RESERVATION_NOT_FOUND, reservationCode));
    }

    private Shop getShopBy(String shopCode) {
        return shopRepository.findByShopCode(shopCode).orElseThrow(() ->
                new ArgumentException(ErrorCode.SHOP_NOT_FOUND, shopCode));
    }

    private Member getMemberBy(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() ->
                new ArgumentException(ErrorCode.MEMBER_NOT_FOUND, email));
    }

    private void isReviewExist(Reservation reservation) {
        if (reviewRepository.existsByReservation(reservation)) {
            throw new ConflictException(ErrorCode.ALREADY_EXIST_REVIEW);
        }
    }

    private static void isVisited(ArrivalStatus arrivalStatus) {
        if (arrivalStatus == ArrivalStatus.N) {
            throw new ConflictException(ErrorCode.SHOP_NOT_VISITED);
        }
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
