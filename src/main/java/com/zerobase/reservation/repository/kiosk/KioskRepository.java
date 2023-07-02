package com.zerobase.reservation.repository.kiosk;

import com.zerobase.reservation.domain.kiosk.Kiosk;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KioskRepository extends JpaRepository<Kiosk, Long>, KioskRepositoryQueryDsl {

    @EntityGraph(attributePaths = "shop")
    Optional<Kiosk> findByKioskCode(String kioskCode);
}
