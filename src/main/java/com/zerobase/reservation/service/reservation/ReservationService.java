package com.zerobase.reservation.service.reservation;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.reservation.ReservationDto;
import com.zerobase.reservation.dto.reservation.SearchConditionReservationDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ConflictException;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.zerobase.reservation.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;

    /**
     * 예약
     */
    @Transactional
    public ReservationDto create(String email, String shopCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        Member member = getMemberBy(email);

        Shop shop = getShopBy(shopCode);

        //예약 종료시간이 예약 시작시간 보다 이전인지 확인
        validateEndTimeIsBefore(startDateTime, endDateTime);

        //해당시간에 예약이 존재하는지 확인
        isReservationExist(startDateTime, endDateTime, shop);

        return ReservationDto.of(
                reservationRepository.save(getReservation(startDateTime, endDateTime, member, shop))
        );
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
        return ReservationDto.of(getReservationBy(reservationCode));
    }


    @Transactional
    public ReservationDto updateArrival(String reservationCode, String shopCode, LocalDateTime now) {
        Reservation reservation = getReservationBy(reservationCode);

        //도착요청 가능한 시간
        LocalDateTime arrivalTimeRange = getArrivalTimeRange(reservation.getStartDateTime());

        //현재 방문시간이 도착요청이 가능한 시간인지 확인
        validateVisitationTime(now, reservation.getEndDateTime(), arrivalTimeRange);

        Shop shop = getShopBy(shopCode);

        //예약된 샵과 방문한 샵이 일치하는지 확인
        isShopMatchingReservation(reservation.getShop(), shop);

        //방문 완료로 변경
        reservation.updateArrivalStatus();

        return ReservationDto.of(reservation);
    }

    private static void isShopMatchingReservation(Shop reservationShop, Shop shop) {
        if (!reservationShop.getShopCode().equals(shop.getShopCode())) {
            throw new ArgumentException(UN_MATCH_SHOP_CODE, shop.getShopCode());
        }
    }

    private static void validateVisitationTime(LocalDateTime now, LocalDateTime endTime, LocalDateTime arrivalTimeRange) {
        if (arrivalTimeRange.isAfter(now) || now.isAfter(endTime)) {
            throw new ConflictException(INVALID_TIME, now.toString());
        }
    }


    private static void validateEndTimeIsBefore(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (endDateTime.isBefore(startDateTime)) {
            throw new ArgumentException(END_TIME_MUST_BE_AFTER_START_TIME, String.format("start[%s], end[%s]", startDateTime, endDateTime));
        }
    }

    private Reservation getReservationBy(String reservationCode) {
        return reservationRepository.findByReservationCode(reservationCode).
                orElseThrow(() -> new ArgumentException(RESERVATION_NOT_FOUND, reservationCode));
    }

    private Shop getShopBy(String shopCode) {
        return shopRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ArgumentException(SHOP_NOT_FOUND, shopCode));
    }

    private Member getMemberBy(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ArgumentException(MEMBER_NOT_FOUND, email));
    }

    private void isReservationExist(LocalDateTime startDateTime, LocalDateTime endDateTime, Shop shop) {
        if (reservationRepository.existReservationBy((startDateTime.plusSeconds(1)), endDateTime, shop).isPresent()) {
            throw new ArgumentException(ALREADY_EXIST_RESERVATION,
                    String.format("%s or %s", startDateTime, endDateTime));
        }
    }


    private LocalDateTime getArrivalTimeRange(LocalDateTime startTime) {
        return startTime.minusMinutes(10);
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
