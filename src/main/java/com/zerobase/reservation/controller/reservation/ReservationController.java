package com.zerobase.reservation.controller.reservation;

import com.zerobase.reservation.dto.kiosk.KioskDto;
import com.zerobase.reservation.dto.reservation.*;
import com.zerobase.reservation.service.kiosk.KioskService;
import com.zerobase.reservation.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    private final KioskService kioskService;

    @PostMapping
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) and (hasRole('USER')))")
    public ResponseEntity<?> create(@Valid @RequestBody CreateReservationDto.Request request) {

        reservationService.create(
                request.getEmail(),
                request.getShopCode(),
                request.getStartDateTime(),
                request.getEndDateTime()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) or (hasRole('SELLER')))")
    public ResponseEntity<Page<ReservationInfoDto.Response>> readAllByCondition(
            SearchConditionReservationDto request,
            @PageableDefault(sort = "startDateTime") Pageable pageable) {
        return ResponseEntity.ok(
                reservationService.getReservationsByCondition(request, pageable)
                        .map(ReservationInfoDto.Response::of)
        );
    }

    @GetMapping("/{reservationCode}")
    @PreAuthorize("isAuthenticated() and (hasRole('USER') or (hasRole('SELLER')))")
    @PostAuthorize("returnObject.body.reservationEmail == principal.username or hasRole('SELLER')")
    public ResponseEntity<ReservationInfoDetailDto.Response> read(@PathVariable String reservationCode) {
        ReservationDto reservation = reservationService.getReservation(reservationCode);
        return ResponseEntity.ok(
                ReservationInfoDetailDto.Response.of(reservation)
        );
    }

    @PutMapping("/{reservationCode}/arrival")
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) and (hasRole('USER')))")
    public ResponseEntity<?> arrival(@PathVariable String reservationCode,
                                     @RequestBody UpdateReservationArrivalDto.Request request) {

        KioskDto kiosk = kioskService.getKiosk(request.getKioskCode());
        reservationService.updateArrival(reservationCode, kiosk.getShop().getShopCode(), LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

}
