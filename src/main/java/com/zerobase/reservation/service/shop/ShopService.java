package com.zerobase.reservation.service.shop;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.shop.MemberShop;
import com.zerobase.reservation.domain.shop.MemberShopId;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.shop.SearchConditionShopDto;
import com.zerobase.reservation.dto.shop.ShopDto;
import com.zerobase.reservation.dto.shop.ShopInfoDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.global.resolver.shop.PageRequest;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.review.ReviewRepository;
import com.zerobase.reservation.repository.shop.MemberShopRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.repository.shop.mybatis.ShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.zerobase.reservation.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.zerobase.reservation.global.exception.ErrorCode.SHOP_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopMapper shopMapper;
    private final ShopRepository shopRepository;
    private final MemberShopRepository memberShopRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 상점 등록
     **/
    @Transactional
    public ShopDto createShop(String email, String name, Double latitude, Double longitude) {

        Member member = getMemberBy(email);
        Shop saveShop = shopRepository.save(getShopBy(name, latitude, longitude));
        memberShopRepository.save(getMemberShopBy(member, saveShop));

        return ShopDto.of(saveShop);
    }


    /**
     * 상점 상세 조회
     */
    public ShopDto getShop(String shopCode) {
        return ShopDto.of(getShopBy(shopCode));
    }


    /**
     * 상점 전체조회 (검색조건 및 정렬)
     */
    public List<ShopInfoDto.Response> getShops(SearchConditionShopDto condition, PageRequest pageRequest) {
        return shopMapper.findAllBySearchConditions(condition, pageRequest);
    }

    /**
     * 상점 수정
     */
    @Transactional
    public ShopDto update(String shopCode, String name, Double longitude, Double latitude) {
        Shop shop = getShopBy(shopCode);
        shop.updateShop(name, latitude, longitude);
        return ShopDto.of(shop);
    }

    /**
     * 상점 삭제
     */
    @Transactional
    public void delete(String shopCode) {
        Shop shop = getShopBy(shopCode);

        deleteChildTables(shop.getId());
        shopRepository.delete(shop);
    }

    private void deleteChildTables(Long shopId) {
        memberShopRepository.deleteByShopId(shopId);
        reviewRepository.deleteByMemberId(shopId);
        reservationRepository.deleteByMemberId(shopId);
    }

    private Member getMemberBy(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ArgumentException(MEMBER_NOT_FOUND, email));
    }


    private Shop getShopBy(String shopCode) {
        return shopRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ArgumentException(SHOP_NOT_FOUND, shopCode));
    }

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

}
