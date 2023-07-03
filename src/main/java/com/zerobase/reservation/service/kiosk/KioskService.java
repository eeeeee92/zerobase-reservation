package com.zerobase.reservation.service.kiosk;

import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.kiosk.KioskDto;
import com.zerobase.reservation.dto.kiosk.SearchConditionKioskDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.global.exception.ServerErrorException;
import com.zerobase.reservation.repository.kiosk.KioskRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.InstallationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KioskService {

    private final KioskRepository kioskRepository;
    private final ShopRepository shopRepository;

    /**
     * 키오스크 단건조회
     */
    @Transactional
    public KioskDto getKiosk(String kioskCode) {
        Kiosk kiosk = kioskRepository.findByKioskCode(kioskCode)
                .orElseThrow(() -> new ServerErrorException(ErrorCode.KIOSK_NOT_FOUND, kioskCode));
        return KioskDto.of(kiosk);
    }

    /**
     * 키오스크 등록
     */
    @Transactional
    public KioskDto registration() {
        return KioskDto.of(kioskRepository.save(Kiosk.builder()
                .build())
        );
    }

    /**
     * 키오스크 설치
     */
    @Transactional
    public KioskDto installation(String shopCode, String kioskCode, LocalDate installationYear, String installationLocation) {
        Shop shop = shopRepository.findByShopCode(shopCode)
                .orElseThrow(() -> new ArgumentException(ErrorCode.SHOP_NOT_FOUND));
        Kiosk kiosk = kioskRepository.findByKioskCode(kioskCode)
                .orElseThrow(() -> new ArgumentException(ErrorCode.KIOSK_NOT_FOUND));

        kiosk.updateKiosk(installationYear, installationLocation, shop, InstallationStatus.Y);
        return KioskDto.of(kiosk);
    }

    /**
     * 키오스크 삭제
     */
    @Transactional
    public void delete(String kioskCode) {
        Kiosk kiosk = kioskRepository.findByKioskCode(kioskCode)
                .orElseThrow(() -> new ArgumentException(ErrorCode.KIOSK_NOT_FOUND, kioskCode));
        kioskRepository.delete(kiosk);
    }

    /**
     * 키오스크 설치 해제
     */
    @Transactional
    public KioskDto unInstall(String kioskCode) {

        Kiosk kiosk = kioskRepository.findByKioskCode(kioskCode)
                .orElseThrow(() -> new ArgumentException(ErrorCode.KIOSK_NOT_FOUND, kioskCode));
        kiosk.updateKiosk(null, null, null, InstallationStatus.N);
        return KioskDto.of(kiosk);
    }

    /**
     * 검색조건별 키오스크 전체 조회
     */
    public Page<KioskDto> getKiosksBy(SearchConditionKioskDto condition, Pageable pageable) {
        return kioskRepository.findAllBySearchConditions(condition, pageable)
                .map(KioskDto::of);
    }
}
