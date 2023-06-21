package com.zerobase.reservation.repository.reservation;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

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
                .build();
        Shop shop = Shop.builder()
                .name("shop1")
                .build();
        LocalDateTime startDateTime = LocalDateTime.of(2022, 05, 29, 0,0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 06, 5, 0,0);
        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        LocalDateTime wantStartDate = LocalDateTime.of(2022, 6, 5, 0, 0,1);
        LocalDateTime wantEndDate = LocalDateTime.of(2022, 6, 6, 12, 0);
        memberRepository.save(member);
        Shop saveShop = shopRepository.save(shop);
        reservationRepository.save(reservation);

        //when
        Optional<Reservation> reservation1 = reservationRepository.confirmReservation(wantStartDate, wantEndDate, saveShop.getId());

        //then
        Assertions.assertTrue(reservation1.isEmpty());

    }
}