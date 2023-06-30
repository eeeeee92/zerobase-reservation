package com.zerobase.reservation.scheduler;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.review.ReviewRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@Transactional
class ShopRatingSchedulerTest {


    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ShopRatingScheduler scheduler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("정해진 시간에 상점에 대한 리뷰 평점들의 평균을 상점 평점에 업데이트한다")
    public void schedulerSuccess() throws Exception {
        //given
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


        memberRepository.save(member);
        shopRepository.save(shop);

        List<Reservation> reservations = reservationRepository.saveAll(IntStream.range(1, 6)
                .mapToObj(value -> Reservation.builder()
                        .member(member)
                        .shop(shop)
                        .startDateTime(LocalDateTime.now())
                        .endDateTime(LocalDateTime.now())
                        .build()
                ).collect(Collectors.toList()));

        IntStream.range(1, 6).mapToObj(value ->
                        Review.builder()
                                .reservation(reservations.get(value-1))
                                .member(member)
                                .shop(shop)
                                .content("콘텐트")
                                .rating(value).build()
                ).forEach(review -> reviewRepository.save(review));

        //when
        scheduler.updateShopRating();

        Shop findShop = shopRepository.findByShopCode(shop.getShopCode())
                .orElse(null);

        //then
        Assertions.assertThat(findShop.getRating()).isEqualTo(3.0);
    }


}