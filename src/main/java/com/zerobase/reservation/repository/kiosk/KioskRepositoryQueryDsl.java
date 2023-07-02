package com.zerobase.reservation.repository.kiosk;

import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.dto.kiosk.SearchConditionKioskDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface KioskRepositoryQueryDsl {
    Page<Kiosk> findAllBySearchConditions(SearchConditionKioskDto searchConditions, Pageable pageable);
}
