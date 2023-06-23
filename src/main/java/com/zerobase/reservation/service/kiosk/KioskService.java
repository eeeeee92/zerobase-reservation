package com.zerobase.reservation.service.kiosk;

import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.dto.kiosk.KioskDto;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.global.exception.ServerErrorException;
import com.zerobase.reservation.repository.kiosk.KioskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KioskService {

    private final KioskRepository kioskRepository;

    @Transactional
    public KioskDto getKiosk(String kioskCode) {
        Kiosk kiosk = kioskRepository.findByKioskCode(kioskCode)
                .orElseThrow(() -> new ServerErrorException(ErrorCode.KIOSK_NOT_FOUND, kioskCode));
        return KioskDto.of(kiosk);
    }
}
