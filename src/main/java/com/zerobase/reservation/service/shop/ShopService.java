package com.zerobase.reservation.service.shop;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.shop.MemberShop;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.shop.ShopDto;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.shop.MemberShopRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {
    private final ShopRepository shopRepository;
    private final MemberShopRepository memberShopRepository;
    private final MemberRepository memberRepository;

    /** 매장 등록 **/
    @Transactional
    public ShopDto createShop(String email, String name, Double latitude, Double longitude){

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));
        Shop saveShop = shopRepository.save(getShopBy(name, latitude, longitude));
        memberShopRepository.save(getMemberShopBy(member, saveShop));

        return ShopDto.of(saveShop);
    }

//    /** 매장 상세 조회 */
//    public ShopDto getShop(Long ShopId) {
//
//    }


    private MemberShop getMemberShopBy(Member member, Shop saveShop) {
        return MemberShop.builder()
                .member(member)
                .shop(saveShop)
                .build();
    }

    private static Shop getShopBy(String name, Double latitude, Double longitude) {
        return Shop.builder()
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    /** 매장 삭제 **/
}
