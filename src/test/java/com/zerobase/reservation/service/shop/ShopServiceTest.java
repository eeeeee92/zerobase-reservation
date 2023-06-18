package com.zerobase.reservation.service.shop;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.dto.shop.ShopDto;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.shop.MemberShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class ShopServiceTest {

    @Autowired
    private ShopService shopService;

    @MockBean
    MemberShopRepository memberShopRepository;

    @MockBean
    MemberRepository memberRepository;

    @Test
    @DisplayName("매장을 등록한다")
    public void createShop() throws Exception {
        //given
        String email = "이메일이다";
        String name = "매장1";
        Double latitude = 123.1;
        Double longitude = 123.2;
        Member member = Member.builder()
                .email(email)
                .build();

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(memberShopRepository.save(any())).willReturn(any());

        //when
        ShopDto dto = shopService.createShop(email, name, latitude, longitude);

        //then
        verify(memberRepository, times(1)).findByEmail(any());
        verify(memberShopRepository, times(1)).save(any());
        assertThat(dto.getId()).isNotNull();
        assertThat(dto)
                .extracting("name", "latitude", "longitude")
                .contains(name, latitude, longitude);
        assertThat(dto.getRating()).isNull();


    }
}