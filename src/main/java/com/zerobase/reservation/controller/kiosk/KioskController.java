package com.zerobase.reservation.controller.kiosk;

import com.zerobase.reservation.dto.kiosk.InstallationKioskDto;
import com.zerobase.reservation.dto.kiosk.KioskInfoDetail;
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

    /**
     * 키오스크 설치
     */
    @PutMapping("/installation/{kioskCode}")
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

    /**
     * 키오스크 등록
     */
    @PostMapping
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN'))")
    public ResponseEntity<?> registration() {
        kioskService.registration();
        return ResponseEntity.ok().build();
    }

    /**
     * 단건 조회
     */
    @GetMapping("/{kioskCode}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN'))")
    public ResponseEntity<KioskInfoDetail.Response> read(@PathVariable String kioskCode) {
        return ResponseEntity.ok(
                KioskInfoDetail.Response.of(
                        kioskService.getKiosk(kioskCode)
                )
        );
    }

    /**
     * 키오스크 삭제
     */
    @DeleteMapping("/{kioskCode}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN'))")
    public ResponseEntity<?> delete(@PathVariable String kioskCode) {
        kioskService.delete(kioskCode);
        return ResponseEntity.ok().build();
    }

    /**
     * 설치 해제
     */
    @PutMapping("/uninstall/{kioskCode}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN'))")
    public ResponseEntity<?> uninstall(@PathVariable String kioskCode) {
        kioskService.unInstall(kioskCode);
        return ResponseEntity.ok().build();
    }


    //TODO 설치되지 않은 단말조회 (검색 조건 별 전체조회)

}
