package com.zerobase.reservation.repository.shop;

import com.zerobase.reservation.domain.shop.MemberShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberShopRepository extends JpaRepository<MemberShop, Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from MemberShop ms where ms.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);
}
