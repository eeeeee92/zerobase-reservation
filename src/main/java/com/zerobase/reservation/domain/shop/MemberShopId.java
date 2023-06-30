package com.zerobase.reservation.domain.shop;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberShopId implements Serializable {

    private Long member;

    private Long shop;

    @Builder
    private MemberShopId(Long member, Long shop) {
        this.member = member;
        this.shop = shop;
    }
}
