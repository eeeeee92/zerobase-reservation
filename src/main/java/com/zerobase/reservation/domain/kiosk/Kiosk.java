package com.zerobase.reservation.domain.kiosk;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.type.InstallationStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Kiosk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @Column(unique = true)
    private String kioskCode;
    private String installationLocation;
    private LocalDate installationYear;
    private InstallationStatus installationStatus;


    @Builder
    private Kiosk(Shop shop, String installationLocation, LocalDate installationYear) {
        this.shop = shop;
        this.kioskCode = UUID.randomUUID().toString();
        this.installationLocation = installationLocation;
        this.installationYear = installationYear;
        this.installationStatus = InstallationStatus.Y;
    }
}
