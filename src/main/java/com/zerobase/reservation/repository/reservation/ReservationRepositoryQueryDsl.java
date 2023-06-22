package com.zerobase.reservation.repository.reservation;

import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.dto.reservation.SearchConditionReservationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationRepositoryQueryDsl {

    Page<Reservation> findAllBySearchConditions(SearchConditionReservationDto searchConditions, Pageable pageable);
}
