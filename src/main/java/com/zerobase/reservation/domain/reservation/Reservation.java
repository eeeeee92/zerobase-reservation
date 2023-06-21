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

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private ArrivalStatus arrivalStatus;

    @Builder
    private Reservation(Shop shop, Member member, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.shop = shop;
        this.member = member;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.arrivalStatus = ArrivalStatus.N;
    }
}
