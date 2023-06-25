package com.zerobase.reservation.service.review;

import com.zerobase.reservation.config.AcceptanceTest;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@AcceptanceTest
@Transactional
class ReviewServiceTest {
    @Autowired
    ReviewService reviewService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    ReviewRepository reviewRepository;


    @Test
    @DisplayName("리뷰 등록")
    public void create() throws Exception {
        //given
        Shop shop = Shop.builder().build();
        Shop saveShop = shopRepository.save(shop);

        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .build();
        Member saveMember = memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(saveMember)
                .shop(saveShop)
                .build();

        Reservation reservation1 = Reservation.builder()
                .member(saveMember)
                .shop(saveShop)
                .build();
        reservation.updateArrivalStatus();
        reservation1.updateArrivalStatus();

        reservationRepository.save(reservation);
        reservationRepository.save(reservation1);
        int rating = 5;
        int rating1 = 0;
        String content = "zerobase";
        String imageUrl = "imageUrl";
        //when
        reviewService.create(email, shop.getShopCode(), reservation.getReservationCode(),
                rating, content, imageUrl);
        ReviewDto reviewDto1 = reviewService.create(email, shop.getShopCode(), reservation1.getReservationCode(),
                rating1, content, imageUrl);


        //then
        Assertions.assertThat(reviewDto1).extracting("rating", "rating", "content", "imageUrl")
                .contains(rating1, content, imageUrl);
        Assertions.assertThat(reviewDto1.getShop().getRating())
                .isEqualTo(2.5);
    }

    @Test
    @DisplayName("리뷰 등록시 예약이 존재하지 않으면 예외가 발생한다")
    public void create_reservationNotFound() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String shopCode = UUID.randomUUID().toString();
        String reservationCode = UUID.randomUUID().toString();
        ArgumentException argumentException = new ArgumentException(ErrorCode.RESERVATION_NOT_FOUND, reservationCode);
        //when //then

        ArgumentException exception = assertThrows(ArgumentException.class,
                () -> reviewService.create(email, shopCode, reservationCode, 1, "content", "imageUrl"));

        Assertions.assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("리뷰 등록시 상점을 방문하지 않았으면 예외가 발생한다")
    public void create_shopNotVisited() throws Exception {
        //given
        Reservation reservation = Reservation.builder().build();
        reservationRepository.save(reservation);
        String email = "zerobase@naver.com";
        String shopCode = UUID.randomUUID().toString();
        //when
        ConflictException exception = assertThrows(ConflictException.class, () ->
                reviewService.create(email, shopCode, reservation.getReservationCode(), 1, "content", "imageUrl"));
        //then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SHOP_NOT_VISITED);
    }

    @Test
    @DisplayName("리뷰 등록시 이미 예약에 대해 리뷰가 존재하면 예외가 발생한다")
    public void create_alreadyExistReservation() throws Exception {
        //given
        Reservation reservation = Reservation.builder().build();
        Reservation saveReservation = reservationRepository.save(reservation);
        String email = "zerobase@naver.com";
        String shopCode = UUID.randomUUID().toString();
        reservation.updateArrivalStatus();

        Review review = Review.builder()
                .reservation(saveReservation)
                .build();
        reviewRepository.save(review);

        //when //then
        ConflictException exception = assertThrows(ConflictException.class, () ->
                reviewService.create(email, shopCode, reservation.getReservationCode(), 1, "content", "imageUrl"));
        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.ALREADY_EXIST_REVIEW);

    }

    @Test
    @DisplayName("리뷰 등록시 회원이 존재하지 않으면 예외가 발생한다")
    public void create_memberNotFound() throws Exception {
        //given
        Reservation reservation = Reservation.builder().build();
        reservationRepository.save(reservation);
        reservation.updateArrivalStatus();
        String email = "zerobase@naver.com";
        String shopCode = UUID.randomUUID().toString();

        ArgumentException argumentException = new ArgumentException(ErrorCode.MEMBER_NOT_FOUND, email);
        //when
        ArgumentException exception = assertThrows(ArgumentException.class, () ->
                reviewService.create(email, shopCode, reservation.getReservationCode(), 1, "content", "imageUrl"));


        //then
        Assertions.assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("리뷰 등록시 상점이 존재하지 않으면 예외가 발생한다")
    public void create_shopNotFound() throws Exception {
        //given
        Reservation reservation = Reservation.builder().build();
        reservationRepository.save(reservation);
        reservation.updateArrivalStatus();
        String email = "zerobase@naver.com";
        String shopCode = UUID.randomUUID().toString();
        Member member = Member.builder()
                .email(email)
                .build();
        memberRepository.save(member);

        ArgumentException argumentException = new ArgumentException(ErrorCode.SHOP_NOT_FOUND, shopCode);
        //when
        ArgumentException exception = assertThrows(ArgumentException.class, () ->
                reviewService.create(email, shopCode, reservation.getReservationCode(), 1, "content", "imageUrl"));


        //then
        Assertions.assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }
}