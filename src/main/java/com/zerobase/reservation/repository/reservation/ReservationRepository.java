package com.zerobase.reservation.repository.reservation;

import com.zerobase.reservation.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r where r.startDateTime between :startDateTime and :endDateTime or r.endDateTime between :startDateTime and :endDateTime")
    Optional<Reservation> confirmReservation(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime")LocalDateTime endDateTime);


}
