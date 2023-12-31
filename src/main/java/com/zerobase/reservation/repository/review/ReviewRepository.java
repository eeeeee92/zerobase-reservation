package com.zerobase.reservation.repository.review;

import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.review.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryQueryDsl {

    @Modifying(clearAutomatically = true)
    @Query("delete from Review r where r.member.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Review r where r.shop.id = :shopId")
    void deleteByShopId(@Param("shopId") Long shopId);

    boolean existsByReservation(Reservation reservation);

    @Query("select r.rating from Review r where r.shop.id = :shopId")
    List<Integer> findAllRatingByShopId(@Param("shopId") Long shopId);


    @EntityGraph(attributePaths = {"member", "shop", "reservation"})
    Optional<Review> findByReviewCode(String reviewCode);
}
