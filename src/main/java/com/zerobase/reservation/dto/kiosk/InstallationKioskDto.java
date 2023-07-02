package com.zerobase.reservation.dto.kiosk;

import lombok.AccessLevel;
import lombok.Builder;
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

        @Builder
        private Request(String shopCode, LocalDate installationYear, String installationLocation) {
            this.shopCode = shopCode;
            this.installationYear = installationYear;
            this.installationLocation = installationLocation;
        }
    }
}
