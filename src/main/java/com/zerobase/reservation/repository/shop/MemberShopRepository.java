package com.zerobase.reservation.repository.shop;

import com.zerobase.reservation.domain.shop.MemberShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberShopRepository extends JpaRepository<MemberShop, Long> {
}
