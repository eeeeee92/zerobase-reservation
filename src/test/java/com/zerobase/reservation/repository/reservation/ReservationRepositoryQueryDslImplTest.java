package com.zerobase.reservation.repository.reservation;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.reservation.SearchConditionReservationDto;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryQueryDslImplTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ShopRepository shopRepository;


    @Test
    public void findAllBySearchConditions_memberIdEq() throws Exception {
        //given
        Member member1 = Member.builder().email("zerobase1@naver.com").build();
        Member member2 = Member.builder().email("zerobase2@naver.com").build();
        Member member3 = Member.builder().email("zerobase3@naver.com").build();
        List<Member> members = Arrays.asList(member1, member2, member3);
        Shop shop = Shop.builder().name("shop1").build();

        LocalDateTime of1 = LocalDateTime.of(2023, 5, 23, 0, 1);
        LocalDateTime of2 = LocalDateTime.of(2023, 5, 24, 0, 1);
        LocalDateTime of3 = LocalDateTime.of(2023, 5, 25, 0, 1);

        List<Member> saveMembers = memberRepository.saveAll(members);
        Shop saveShop = shopRepository.save(shop);

        LocalDateTime end1 = LocalDateTime.of(2023, 5, 26, 0, 1);
        LocalDateTime end2 = LocalDateTime.of(2023, 5, 27, 0, 1);
        LocalDateTime end3 = LocalDateTime.of(2023, 5, 28, 0, 1);

        Reservation reservation1 = getBuild(saveMembers.get(0), saveShop, of1, end1);
        Reservation reservation2 = getBuild(saveMembers.get(0), saveShop, of2, end2);
        Reservation reservation3 = getBuild(saveMembers.get(1), saveShop, of3, end3);
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2, reservation3);


        reservationRepository.saveAll(reservations);
        SearchConditionReservationDto dto = SearchConditionReservationDto.builder()
                .email(member1.getEmail())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        //when
        Page<Reservation> reservationPage = reservationRepository.findAllBySearchConditions(dto, pageRequest);


        //then
        assertThat(reservationPage.getContent().size()).isEqualTo(2);
    }

    @Test
    public void findAllBySearchConditions_shopIdEq() throws Exception {
        //given
        Member member = Member.builder().email("zerobase1@naver.com").build();

        Shop shop1 = Shop.builder().name("shop1").build();
        Shop shop2 = Shop.builder().name("shop2").build();
        Shop shop3 = Shop.builder().name("shop3").build();
        List<Shop> shops = Arrays.asList(shop1, shop2, shop3);

        LocalDateTime of1 = LocalDateTime.of(2023, 5, 23, 0, 1);
        LocalDateTime of2 = LocalDateTime.of(2023, 5, 24, 0, 1);
        LocalDateTime of3 = LocalDateTime.of(2023, 5, 25, 0, 1);

        Member saveMember = memberRepository.save(member);
        shopRepository.saveAll(shops);

        LocalDateTime end1 = LocalDateTime.of(2023, 5, 26, 0, 1);
        LocalDateTime end2 = LocalDateTime.of(2023, 5, 27, 0, 1);
        LocalDateTime end3 = LocalDateTime.of(2023, 5, 28, 0, 1);

        Reservation reservation1 = getBuild(member, shops.get(0), of1, end1);
        Reservation reservation2 = getBuild(member, shops.get(0), of2, end2);
        Reservation reservation3 = getBuild(member, shops.get(1), of3, end3);
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2, reservation3);


        reservationRepository.saveAll(reservations);
        SearchConditionReservationDto dto = SearchConditionReservationDto.builder()
                .shopCode(shops.get(0).getShopCode())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        //when
        Page<Reservation> reservationPage = reservationRepository.findAllBySearchConditions(dto, pageRequest);


        //then
        assertThat(reservationPage.getContent().size()).isEqualTo(2);
    }

    @Test
    public void findAllBySearchConditions_startDateBetween() throws Exception {
        //given
        Member member = Member.builder().email("zerobase1@naver.com").build();

        Shop shop = Shop.builder().name("shop1").build();

        LocalDateTime of1 = LocalDateTime.of(2023, 5, 23, 0, 0);
        LocalDateTime of2 = LocalDateTime.of(2023, 5, 23, 23, 59);
        LocalDateTime of3 = LocalDateTime.of(2023, 5, 24, 0, 0);

        Member saveMember = memberRepository.save(member);
        Shop saveShop = shopRepository.save(shop);

        LocalDateTime end1 = LocalDateTime.of(2023, 5, 26, 0, 1);
        LocalDateTime end2 = LocalDateTime.of(2023, 5, 27, 0, 1);
        LocalDateTime end3 = LocalDateTime.of(2023, 5, 28, 0, 1);

        Reservation reservation1 = getBuild(member, saveShop, of1, end1);
        Reservation reservation2 = getBuild(member, saveShop, of2, end2);
        Reservation reservation3 = getBuild(member, saveShop, of3, end3);
        List<Reservation> reservations = Arrays.asList(reservation1, reservation2, reservation3);


        reservationRepository.saveAll(reservations);
        SearchConditionReservationDto dto = SearchConditionReservationDto.builder()
                .date(LocalDate.of(2023, 5, 23))
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        //when
        Page<Reservation> reservationPage = reservationRepository.findAllBySearchConditions(dto, pageRequest);


        //then
        assertThat(reservationPage.getContent().size()).isEqualTo(2);
    }

    private static Reservation getBuild(Member member, Shop shop, LocalDateTime startDate, LocalDateTime endDate) {
        return Reservation.builder().member(member).shop(shop).startDateTime(startDate).endDateTime(endDate).build();
    }
}