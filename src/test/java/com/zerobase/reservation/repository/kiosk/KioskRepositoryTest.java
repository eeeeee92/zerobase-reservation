package com.zerobase.reservation.repository.kiosk;

import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.InstallationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class KioskRepositoryTest {

    @Autowired
    private KioskRepository kioskRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Test
    public void findByKioskCode() throws Exception {
        //given

        Shop shop = Shop.builder()
                .name("상점1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        String installationLocation = "1층 현관";
        LocalDate installationYear = LocalDate.of(2022, 05, 23);
        Kiosk kiosk = Kiosk.builder()
                .shop(shop)
                .installationLocation(installationLocation)
                .installationYear(installationYear)
                .build();
        Kiosk saveKiosk = kioskRepository.save(kiosk);

        //when
        Kiosk findKiosk = kioskRepository.findByKioskCode(kiosk.getKioskCode())
                .orElse(null);

        //then
        assertThat(findKiosk)
                .extracting("id", "kioskCode", "installationLocation", "installationYear", "installationStatus")
                .contains(
                        saveKiosk.getId(), kiosk.getKioskCode(),
                        installationLocation, installationYear, InstallationStatus.Y
                );
        assertThat(findKiosk.getShop().getShopCode()).isEqualTo(shop.getShopCode());
    }
}