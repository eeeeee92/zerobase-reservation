package com.zerobase.reservation.repository.reservation;

import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryQueryDsl {

    @Query("select r from Reservation r " +
            "where " +
            "r.shop = :shop " +
            "and r.startDateTime <= :endDateTime " +
            "and r.endDateTime >= :startDateTime")
    Optional<Reservation> existReservationBy(@Param("startDateTime") LocalDateTime startDateTime,
                                             @Param("endDateTime") LocalDateTime endDateTime,
                                             @Param("shop") Shop shop);


    @EntityGraph(attributePaths = {"member", "shop"})
    Optional<Reservation> findByReservationCode(String reservationCode);

    @Modifying(clearAutomatically = true)
    @Query("delete from Reservation r where r.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Reservation r where r.shop.id = :shopId")
    void deleteByShopId(@Param("shopId") Long shopId);
}
