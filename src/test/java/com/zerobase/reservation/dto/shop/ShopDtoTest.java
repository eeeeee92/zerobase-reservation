package com.zerobase.reservation.dto.shop;

import com.zerobase.reservation.domain.shop.Shop;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShopDtoTest {

    @Test
    public void of() throws Exception {
        //given
        Shop shop = Shop.builder()
                .name("샵1")
                .latitude(12.0)
                .longitude(12.1)
                .build();

        //when
        ShopDto shopDto = ShopDto.of(shop);
        //then
        assertThat(shopDto).extracting("id", "name", "latitude", "longitude", "rating")
                .contains(null, "샵1", 12.0, 12.1, null);
    }
}