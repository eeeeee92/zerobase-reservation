package com.zerobase.reservation.repository.shop;

import com.zerobase.reservation.domain.shop.MemberShop;
import com.zerobase.reservation.domain.shop.MemberShopId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberShopRepository extends JpaRepository<MemberShop, MemberShopId> {

    @Modifying(clearAutomatically = true)
    @Query("delete from MemberShop ms where ms.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("delete from MemberShop ms where ms.shop.id = :shopId")
    void deleteByShopId(@Param("shopId") Long shopId);
}
