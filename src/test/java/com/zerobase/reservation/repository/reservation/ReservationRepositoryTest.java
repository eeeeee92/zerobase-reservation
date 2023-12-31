package com.zerobase.reservation.repository.reservation;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.ArrivalStatus;
import com.zerobase.reservation.type.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Test
    public void findByStartBetweenOrEndBetween() throws Exception {
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
        LocalDateTime startDateTime = LocalDateTime.of(2022, 05, 29, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 06, 5, 0, 0);
        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        LocalDateTime wantStartDate = LocalDateTime.of(2022, 6, 5, 0, 0, 1);
        LocalDateTime wantEndDate = LocalDateTime.of(2022, 6, 6, 12, 0);
        memberRepository.save(member);
        Shop saveShop = shopRepository.save(shop);
        reservationRepository.save(reservation);

        //when
        Optional<Reservation> reservation1 = reservationRepository.existReservationBy(wantStartDate, wantEndDate, shop);

        //then
        Assertions.assertTrue(reservation1.isEmpty());

    }

    @Test
    public void findByReservationCode() throws Exception {
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
        LocalDateTime startDateTime = LocalDateTime.of(2022, 05, 23, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 05, 23, 13, 0);


        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        memberRepository.save(member);
        shopRepository.save(shop);
        Reservation saveReservation = reservationRepository.save(reservation);

        //when
        Reservation findReservation = reservationRepository.findByReservationCode(saveReservation.getReservationCode())
                .orElse(null);

        //then
        assertThat(findReservation).extracting("reservationCode", "startDateTime", "endDateTime", "arrivalStatus")
                .contains(reservation.getReservationCode(), startDateTime, endDateTime, ArrivalStatus.N);

    }
}