package com.zerobase.reservation.dto.kiosk;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

public class KioskInfoDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {

        private String shopName;
        private String shopCode;
        private String kioskCode;
        private String installationLocation;
        private LocalDate installationYear;
        private String installationStatus;

        @Builder
        private Response(String shopName, String shopCode, String kioskCode, String installationLocation, LocalDate installationYear, String installationStatus) {
            this.shopName = shopName;
            this.shopCode = shopCode;
            this.kioskCode = kioskCode;
            this.installationLocation = installationLocation;
            this.installationYear = installationYear;
            this.installationStatus = installationStatus;
        }

        public static Response of(KioskDto kioskDto) {

            ResponseBuilder builder = Response.builder()
                    .kioskCode(kioskDto.getKioskCode())
                    .installationLocation(kioskDto.getInstallationLocation())
                    .installationYear(kioskDto.getInstallationYear())
                    .installationStatus(kioskDto.getInstallationStatus().getDescription());

            if (!ObjectUtils.isEmpty(kioskDto.getShop())) {
                builder.shopCode(kioskDto.getShop().getShopCode())
                        .shopName(kioskDto.getShop().getName());

            }
            return builder.build();
        }

    }
}
