package com.zerobase.reservation.dto.kiosk;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public class InstallationKioskDto {

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {
        private String shopCode;
        private LocalDate installationYear;
        private String installationLocation;
    }
}
