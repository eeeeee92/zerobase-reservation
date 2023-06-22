package com.zerobase.reservation.controller.reservation;

import com.zerobase.reservation.dto.reservation.CreateReservationDto;
import com.zerobase.reservation.dto.reservation.ReservationInfoDto;
import com.zerobase.reservation.dto.reservation.SearchConditionReservationDto;
import com.zerobase.reservation.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) and (hasRole('USER')))")
    public ResponseEntity<?> create(@Valid @RequestBody CreateReservationDto.Request request) {

        reservationService.create(
                request.getEmail(),
                request.getShopId(),
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
}
