package com.zerobase.reservation.controller.kiosk;

import com.zerobase.reservation.dto.kiosk.InstallationKioskDto;
import com.zerobase.reservation.service.kiosk.KioskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kiosks")
@Slf4j
public class KioskController {

    private final KioskService kioskService;

    @PutMapping("/{kioskCode}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN'))")
    public ResponseEntity<?> installation(@PathVariable String kioskCode,
                                          @RequestBody InstallationKioskDto.Request request) {
        kioskService.installation(
                request.getShopCode(),
                kioskCode,
                request.getInstallationYear(),
                request.getInstallationLocation()
        );

        return ResponseEntity.ok().build();
    }

}
