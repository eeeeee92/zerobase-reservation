package com.zerobase.reservation.service.reservation;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.reservation.ReservationDto;
import com.zerobase.reservation.dto.reservation.SearchConditionReservationDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.zerobase.reservation.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;

    /**
     * 예약
     */
    public ReservationDto create(String email, String shopCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ArgumentException(MEMBER_NOT_FOUND, email));
        Shop shop = shopRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ArgumentException(SHOP_NOT_FOUND, shopCode));

        if (endDateTime.isBefore(startDateTime)) {
            throw new ArgumentException(END_TIME_MUST_BE_AFTER_START_TIME, String.format("start[%s], end[%s]", startDateTime, endDateTime));
        }


        if (reservationRepository.confirmReservation((startDateTime.plusSeconds(1)), endDateTime, shop).isPresent()) {
            throw new ArgumentException(ALREADY_EXIST_RESERVATION,
                    String.format("%s or %s", startDateTime, endDateTime));
        }

        Reservation reservation = reservationRepository.save(getReservation(startDateTime, endDateTime, member, shop));
        return ReservationDto.of(reservation);
    }

    /**
     * 예약 확인
     */
    public Page<ReservationDto> getReservationsByCondition(SearchConditionReservationDto condition, Pageable pageable) {
        return reservationRepository.findAllBySearchConditions(condition, pageable)
                .map(ReservationDto::of);
    }

    /**
     * 예약 상세조회
     */
    public ReservationDto getReservation(String reservationCode) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode).
                orElseThrow(() -> new ArgumentException(RESERVATION_NOT_FOUND, reservationCode));
        return ReservationDto.of(reservation);
    }


    private static Reservation getReservation(LocalDateTime startDateTime, LocalDateTime endDateTime, Member member, Shop shop) {
        return Reservation.builder()
                .shop(shop)
                .member(member)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }
}
