package com.zerobase.reservation.dto.reservation;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.type.ArrivalStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReservationDto {
    private Long id;
    private Shop shop;
    private Member member;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ArrivalStatus arrivalStatus;


    @Builder
    private ReservationDto(Long id, Shop shop, Member member, LocalDateTime startDateTime, LocalDateTime endDateTime, ArrivalStatus arrivalStatus) {
        this.id = id;
        this.shop = shop;
        this.member = member;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.arrivalStatus = arrivalStatus;
    }

    public static final ReservationDto of(Reservation reservation){
        return ReservationDto.builder()
                .shop(reservation.getShop())
                .member(reservation.getMember())
                .startDateTime(reservation.getStartDateTime())
                .endDateTime(reservation.getEndDateTime())
                .arrivalStatus(reservation.getArrivalStatus())
                .build();
    }
}
