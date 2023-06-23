package com.zerobase.reservation.repository.shop;

import com.zerobase.reservation.domain.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByShopCode(String shopCode);
}
