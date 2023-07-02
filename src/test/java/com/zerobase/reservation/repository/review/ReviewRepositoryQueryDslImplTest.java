package com.zerobase.reservation.repository.review;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.review.SearchConditionReviewDto;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;

@DataJpaTest
class ReviewRepositoryQueryDslImplTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("회원이메일 검색조건별 리뷰 전체조회")
    public void findAllBySearchConditions_emailEq() throws Exception {
        //given
        Member member = Member.builder()
                .email("zerobase@naver.com")
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Member member1 = Member.builder()
                .email("zerobase1@naver.com")
                .nickname("닉네임1")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);
        memberRepository.save(member1);

        Reservation reservation1 = getReservation(member, shop);
        reservation1.updateArrivalStatus();
        Reservation reservation2 = getReservation(member, shop);
        reservation2.updateArrivalStatus();
        Reservation reservation3 = getReservation(member, shop);
        reservation3.updateArrivalStatus();
        Reservation reservation4 = getReservation(member, shop);
        reservation4.updateArrivalStatus();
        Reservation reservation5 = getReservation(member1, shop);
        reservation5.updateArrivalStatus();


        reservationRepository.saveAll(
                Arrays.asList(reservation1, reservation2, reservation3, reservation4, reservation5)
        );


        reviewRepository.saveAll(
                Arrays.asList(
                        getReview(member, shop, reservation1),
                        getReview(member, shop, reservation2),
                        getReview(member, shop, reservation3),
                        getReview(member, shop, reservation4),
                        getReview(member1, shop, reservation5)
                )
        );

        SearchConditionReviewDto condition = SearchConditionReviewDto.builder()
                .email("zerobase@naver.com")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 5);


        //when
        Page<Review> reviews = reviewRepository.findAllBySearchConditions(condition, pageRequest);

        //then
        Assertions.assertThat(reviews.getContent())
                .hasSize(4);
    }

    @Test
    @DisplayName("상점 검색조건별 리뷰 전체조회")
    public void findAllBySearchConditions_shopEq() throws Exception {
        //given
        Member member = Member.builder()
                .email("zerobase@naver.com")
                .nickname("닉네임")
                .role(Role.USER)
                .build();


        Shop shop = Shop.builder()
                .name("shop")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        Shop shop1 = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        shopRepository.save(shop1);
        memberRepository.save(member);


        Reservation reservation1 = getReservation(member, shop);
        reservation1.updateArrivalStatus();
        Reservation reservation2 = getReservation(member, shop);
        reservation2.updateArrivalStatus();
        Reservation reservation3 = getReservation(member, shop);
        reservation3.updateArrivalStatus();
        Reservation reservation4 = getReservation(member, shop);
        reservation4.updateArrivalStatus();
        Reservation reservation5 = getReservation(member, shop1);
        reservation5.updateArrivalStatus();


        reservationRepository.saveAll(
                Arrays.asList(reservation1, reservation2, reservation3, reservation4, reservation5)
        );


        reviewRepository.saveAll(
                Arrays.asList(
                        getReview(member, shop, reservation1),
                        getReview(member, shop, reservation2),
                        getReview(member, shop, reservation3),
                        getReview(member, shop, reservation4),
                        getReview(member, shop1, reservation5)
                )
        );

        SearchConditionReviewDto condition = SearchConditionReviewDto.builder()
                .shopCode(shop.getShopCode())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 5);


        //when
        Page<Review> reviews = reviewRepository.findAllBySearchConditions(condition, pageRequest);

        //then
        Assertions.assertThat(reviews.getContent())
                .hasSize(4);
    }

    private static Review getReview(Member member, Shop shop, Reservation reservation1) {
        return Review.builder()
                .member(member)
                .shop(shop)
                .reservation(reservation1)
                .imageUrl("imageUrl")
                .rating(1)
                .content("content")
                .build();
    }

    private static Reservation getReservation(Member member, Shop shop) {
        return Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();
    }
}