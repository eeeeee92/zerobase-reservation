package com.zerobase.reservation.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Sort {

    private final String property;
    private final Direction direction;

    public enum Direction {
        ASC,
        DESC;
    }
}
