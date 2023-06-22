package com.zerobase.reservation.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArrivalStatus {
    Y("방문 완료"),
    N("방문 미완료");

    private final String description;
}
