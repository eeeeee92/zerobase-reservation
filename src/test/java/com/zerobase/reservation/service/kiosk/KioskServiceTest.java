package com.zerobase.reservation.service.kiosk;

import com.zerobase.reservation.config.AcceptanceTest;
import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.kiosk.KioskDto;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.global.exception.ServerErrorException;
import com.zerobase.reservation.repository.kiosk.KioskRepository;
import com.zerobase.reservation.type.InstallationStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@AcceptanceTest
class KioskServiceTest {

    @Autowired
    private KioskService kioskService;

    @MockBean
    private KioskRepository kioskRepository;

    @Test
    @DisplayName("키오스크를 조회한다")
    public void getKiosk() throws Exception {

        Shop shop = Shop.builder().build();

        String installationLocation = "1층 현관";
        LocalDate installationYear = LocalDate.of(2022, 05, 23);
        Kiosk kiosk = Kiosk.builder()
                .shop(shop)
                .installationLocation(installationLocation)
                .installationYear(installationYear)
                .build();

        //given
        given(kioskRepository.findByKioskCode(any()))
                .willReturn(Optional.of(kiosk));

        //when
        KioskDto kioskDto = kioskService.getKiosk(kiosk.getKioskCode());

        //then
        verify(kioskRepository, times(1)).findByKioskCode(any());
        assertThat(kioskDto)
                .extracting("kioskCode", "installationLocation", "installationYear", "installationStatus")
                .contains(
                        kiosk.getKioskCode(), installationLocation,
                        installationYear, InstallationStatus.N
                );
        assertThat(kioskDto.getShop()).isNotNull();
    }

    @Test
    @DisplayName("키오스크 조회시 키오스크가 존재하지 않으면 예외가 발생한다")
    public void getKiosk_kioskNotFound() throws Exception {
        //given
        String kioskCode = UUID.randomUUID().toString();
        given(kioskRepository.findByKioskCode(any())).willReturn(Optional.empty());
        ServerErrorException serverErrorException = new ServerErrorException(ErrorCode.KIOSK_NOT_FOUND, kioskCode);

        //when  //then
        ServerErrorException exception = assertThrows(ServerErrorException.class, () -> kioskService.getKiosk(kioskCode));

        assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(serverErrorException.getErrorCode(), serverErrorException.getErrorMessage());
    }

    @Test
    @DisplayName("키오스크 등록")
    public void registration() throws Exception {
        //given
        Kiosk kiosk = Kiosk.builder()
                .build();
        given(kioskRepository.save(any()))
                .willReturn(kiosk);

        //when
        KioskDto kioskDto = kioskService.registration();

        //then
        Assertions.assertThat(kioskDto)
                .extracting("kioskCode","installationStatus")
                .contains(kiosk.getKioskCode(), InstallationStatus.N);
    }

    @Test
    @DisplayName("키오스크 삭제")
    public void delete() throws Exception {
        //given
        String kioskCode = UUID.randomUUID().toString();
        given(kioskRepository.findByKioskCode(any()))
                .willReturn(Optional.of(
                        Kiosk.builder().build())
                );

        //when
        kioskService.delete(kioskCode);

        //then
        verify(kioskRepository, times(1)).findByKioskCode(any());
        verify(kioskRepository, times(1)).delete(any());
    }


}