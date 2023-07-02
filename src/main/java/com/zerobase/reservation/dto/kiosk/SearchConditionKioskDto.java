package com.zerobase.reservation.dto.kiosk;

import com.zerobase.reservation.type.InstallationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchConditionKioskDto {

    private String shopCode;
    private InstallationStatus installationStatus;

    @Builder
    private SearchConditionKioskDto(String shopCode, InstallationStatus installationStatus) {
        this.shopCode = shopCode;
        this.installationStatus = installationStatus;
    }


}
