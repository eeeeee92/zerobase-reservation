package com.zerobase.reservation.repository.kiosk;

import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.kiosk.SearchConditionKioskDto;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.InstallationStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DataJpaTest
class KioskRepositoryQueryDslImplTest {


    @Autowired
    private KioskRepository kioskRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Test
    @DisplayName("상점코드 조건 입력시 상점에 포함된 키오스크들이 조회돼야 한다")
    public void findAllBySearchConditions_shopCodeEq() throws Exception {
        //given
        Shop shop = Shop.builder()
                .latitude(12.0)
                .longitude(12.3)
                .name("상점")
                .build();
        shopRepository.save(shop);


        Kiosk kiosk = Kiosk.builder().build();
        kioskRepository.save(kiosk);
        kioskRepository.saveAll(IntStream.range(1, 6)
                .mapToObj(value -> Kiosk.builder()
                        .shop(shop)
                        .build()
                ).collect(Collectors.toList()));


        SearchConditionKioskDto condition = SearchConditionKioskDto.builder()
                .shopCode(shop.getShopCode())
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        List<Kiosk> content = kioskRepository.findAllBySearchConditions(condition, pageRequest).getContent();

        //then
        Assertions.assertThat(content).hasSize(5);
    }


    @Test
    @DisplayName("설치여부 조건 입력시 상점에 포함된 키오스크들이 조회돼야 한다")
    public void findAllBySearchConditions_installationStatusEq() throws Exception {
        //given
        Shop shop = Shop.builder()
                .latitude(12.0)
                .longitude(12.3)
                .name("상점")
                .build();
        shopRepository.save(shop);


        Kiosk kiosk = Kiosk.builder().build();
        kiosk.updateKiosk(null, null, shop, InstallationStatus.Y);
        kioskRepository.save(kiosk);
        kioskRepository.saveAll(IntStream.range(1, 6)
                .mapToObj(value -> Kiosk.builder()
                        .build()
                ).collect(Collectors.toList()));


        SearchConditionKioskDto condition = SearchConditionKioskDto.builder()
                .installationStatus(InstallationStatus.N)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        List<Kiosk> content = kioskRepository.findAllBySearchConditions(condition, pageRequest).getContent();

        //then
        Assertions.assertThat(content).hasSize(5);
    }

}