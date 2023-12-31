package com.zerobase.reservation.domain.reservation;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.type.ArrivalStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(unique = true, nullable = false)
    private String reservationCode;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArrivalStatus arrivalStatus;


    @Builder
    private Reservation(Shop shop, Member member, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.reservationCode = UUID.randomUUID().toString();
        this.shop = shop;
        this.member = member;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.arrivalStatus = ArrivalStatus.N;
    }

    public void updateReservation(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void updateArrivalStatus() {
        this.arrivalStatus = ArrivalStatus.Y;
    }
}
