package com.zerobase.reservation.domain.shop;

import com.zerobase.reservation.repository.shop.ShopRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ShopTest {

    @Autowired
    private ShopRepository shopRepository;

    @Test
    @DisplayName("평점이 업데이트 돼야 한다")
    public void updateRating() throws Exception {
        //given
        Shop shop = Shop.builder()
                .name("상점1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();
        shopRepository.save(shop);

        //when
        shop.updateRating(3.0);

        //then
        Shop findShop = shopRepository.findByShopCode(shop.getShopCode())
                .orElse(null);

        Assertions.assertThat(findShop.getRating())
                .isEqualTo(3.0);

    }
}