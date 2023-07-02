package com.zerobase.reservation.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InstallationStatus {
    Y("설치 완료"),
    N("설치 미완료");

    private final String description;
}
