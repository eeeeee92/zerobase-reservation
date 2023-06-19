package com.zerobase.reservation.service.shop;

import com.zerobase.reservation.config.AcceptanceTest;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.shop.MemberShop;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.shop.ShopDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.shop.MemberShopRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@AcceptanceTest
class ShopServiceTest {

    @Autowired
    private ShopService shopService;

    @MockBean
    MemberShopRepository memberShopRepository;

    @MockBean
    ShopRepository shopRepository;

    @MockBean
    MemberRepository memberRepository;


    @Test
    @DisplayName("상점을 등록한다")
    public void createShop() throws Exception {
        //given
        String email = "이메일이다";
        String name = "매장1";
        Double latitude = 123.1;
        Double longitude = 123.2;
        Member member = Member.builder()
                .email(email)
                .build();

        Shop shop = getShopEntity(name, latitude, longitude);

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(memberShopRepository.save(any())).willReturn(MemberShop.builder()
                        .member(member)
                        .shop(shop)
                .build());
        given(shopRepository.save(any())).willReturn(shop);

        //when
        ShopDto dto = shopService.createShop(email, name, latitude, longitude);

        //then
        verify(memberRepository, times(1)).findByEmail(any());
        verify(memberShopRepository, times(1)).save(any());

        assertThat(dto)
                .extracting("name", "latitude", "longitude")
                .contains(name, latitude, longitude);

    }

    private static Shop getShopEntity(String name, Double latitude, Double longitude) {
        return Shop.builder()
                .name(name)
                .rating(1.0)
                .longitude(latitude)
                .latitude(longitude)
                .build();
    }

    @Test
    @DisplayName("상점을 등록할 때 회원이 존재하지 않으면 예외가 발생한다.")
    public void createShop_MemberNotFound() throws Exception {
        //given
        String email = "이메일이다";
        String name = "매장1";
        Double latitude = 123.1;
        Double longitude = 123.2;

        given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());
        ArgumentException argumentException = new ArgumentException(ErrorCode.MEMBER_NOT_FOUND, email);

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> shopService.createShop(email, name, latitude, longitude));
        assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("상점 상세 조회")
    public void getShop() throws Exception {
        //given
        String name = "샵1";
        double latitude = 12.0;
        double longitude = 12.1;
        double rating = 1.0;
        given(shopRepository.findById(any())).willReturn(
                Optional.of(
                        getShopEntity(name, latitude, longitude, rating))
        );

        //when
        ShopDto shopDto = shopService.getShop(1L);

        //then
        assertThat(shopDto).extracting("name", "latitude", "longitude", "rating")
                .contains(name, latitude, longitude, rating);

    }


    @Test
    @DisplayName("상점 상세조회시 상점이 존재하지 않으면 예외가 발생한다.")
    public void getShop_ShopNotFound() throws Exception {
        //given
        Long shopId = 1L;
        given(shopRepository.findById(any())).willReturn(Optional.empty());
        ArgumentException argumentException = new ArgumentException(ErrorCode.SHOP_NOT_FOUND, String.valueOf(shopId));

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> shopService.getShop(shopId));
        assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    private static Shop getShopEntity(String name, double latitude, double longitude, double rating) {
        return Shop.builder()
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .rating(rating)
                .build();
    }
}