package com.zerobase.reservation.dto.kiosk;

import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.type.InstallationStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class KioskDto {

    private Shop shop;
    private String kioskCode;
    private String installationLocation;
    private LocalDate installationYear;
    private InstallationStatus installationStatus;

    @Builder
    private KioskDto(Shop shop, String kioskCode, String installationLocation, LocalDate installationYear, InstallationStatus installationStatus) {
        this.shop = shop;
        this.kioskCode = kioskCode;
        this.installationLocation = installationLocation;
        this.installationYear = installationYear;
        this.installationStatus = installationStatus;
    }

    public static KioskDto of(Kiosk kiosk) {
        return KioskDto.builder()
                .shop(kiosk.getShop())
                .kioskCode(kiosk.getKioskCode())
                .installationLocation(kiosk.getInstallationLocation())
                .installationYear(kiosk.getInstallationYear())
                .installationStatus(kiosk.getInstallationStatus())
                .build();
    }
}
