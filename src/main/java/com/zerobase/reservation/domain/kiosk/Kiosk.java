package com.zerobase.reservation.domain.kiosk;

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
    @Column(name = "kiosk_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(unique = true, nullable = false)
    private String kioskCode;

    private String installationLocation;

    private LocalDate installationYear;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InstallationStatus installationStatus;


    @Builder
    private Kiosk(Shop shop, String installationLocation, LocalDate installationYear) {
        this.shop = shop;
        this.kioskCode = UUID.randomUUID().toString();
        this.installationLocation = installationLocation;
        this.installationYear = installationYear;
        this.installationStatus = InstallationStatus.N;
    }

    public void updateKiosk(LocalDate installationYear, String installationLocation, Shop shop, InstallationStatus installationStatus) {
        this.installationYear = installationYear;
        this.installationLocation = installationLocation;
        this.shop = shop;
        this.installationStatus = installationStatus;
    }


}
