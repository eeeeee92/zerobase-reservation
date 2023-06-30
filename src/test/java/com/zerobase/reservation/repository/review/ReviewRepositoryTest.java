package com.zerobase.reservation.repository.review;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {


    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void findAllRatingByShopId() throws Exception {

        Member member = Member.builder()
                .email("zerobase@naver.com")
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();



        Shop saveShop = shopRepository.save(shop);
        memberRepository.save(member);

        List<Reservation> reservations = reservationRepository.saveAll(IntStream.range(1, 6)
                .mapToObj(value -> Reservation.builder()
                        .member(member)
                        .shop(shop)
                        .startDateTime(LocalDateTime.now())
                        .endDateTime(LocalDateTime.now())
                        .build()
                ).collect(Collectors.toList()));

        //given
        IntStream.range(1, 6).mapToObj(value -> Review.builder()
                        .reservation(reservations.get(value -1))
                        .member(member)
                        .content("콘텐트")
                        .shop(saveShop).rating(value).build())
                .collect(Collectors.toList()).forEach(review -> reviewRepository.save(review));


        //when
        List<Integer> ratings = reviewRepository.findAllRatingByShopId(saveShop.getId());

        //then
        Assertions.assertThat(ratings)
                .containsExactlyInAnyOrder(1, 2, 3, 4, 5)
                        .hasSize(5);

    }
}