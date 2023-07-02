package com.zerobase.reservation.service.kiosk;

import com.zerobase.reservation.config.AcceptanceTest;
import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.kiosk.KioskDto;
import com.zerobase.reservation.dto.kiosk.SearchConditionKioskDto;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.global.exception.ServerErrorException;
import com.zerobase.reservation.repository.kiosk.KioskRepository;
import com.zerobase.reservation.type.InstallationStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                .extracting("kioskCode", "installationStatus")
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

    @Test
    @DisplayName("키오스크 설치해제")
    public void unInstall() throws Exception {
        //given
        String kioskCode = UUID.randomUUID().toString();
        given(kioskRepository.findByKioskCode(any()))
                .willReturn(Optional.of(
                        Kiosk.builder()
                                .shop(Shop.builder().build())
                                .installationYear(LocalDate.of(2023, 05, 23))
                                .installationLocation("현관")
                                .build()
                ));

        //when
        KioskDto kioskDto = kioskService.unInstall(kioskCode);

        //then
        Assertions.assertThat(kioskDto)
                .extracting("shop", "kioskCode", "installationYear", "installationLocation")
                .containsNull();
        Assertions.assertThat(kioskDto.getInstallationStatus())
                .isEqualTo(InstallationStatus.N);

    }

    @Test
    @DisplayName("검색조건별 키오스크 전체조회")
    public void getKiosksBy() throws Exception {
        //given
        String shopCode = UUID.randomUUID().toString();

        SearchConditionKioskDto condition = SearchConditionKioskDto.builder()
                .shopCode(shopCode)
                .build();
        Shop shop = Shop.builder()
                .name("상점")
                .build();

        List<Kiosk> kiosk = IntStream.range(1, 6)
                .mapToObj(value ->
                        Kiosk.builder()
                                .shop(shop)
                                .installationLocation(String.format("현관 %d층", value))
                                .installationYear(LocalDate.of(2022, 5, 2 + value))
                                .build())
                .collect(Collectors.toList());
        kiosk.forEach(value -> value.updateKiosk(value.getInstallationYear(), value.getInstallationLocation(), value.getShop(), InstallationStatus.Y));
        PageImpl<Kiosk> pageKiosks = new PageImpl<>(kiosk);

        PageRequest pageRequest = PageRequest.of(0, 5);
        given(kioskRepository.findAllBySearchConditions(any(), any()))
                .willReturn(pageKiosks);

        //when
        Page<KioskDto> result = kioskService.getKiosksBy(condition, pageRequest);

        //then
        Assertions.assertThat(result.getContent())
                .extracting("shop", "installationLocation", "installationYear", "installationStatus")
                .containsExactlyInAnyOrder(
                        tuple(shop, "현관 1층", LocalDate.of(2022, 5, 3), InstallationStatus.Y),
                        tuple(shop, "현관 2층", LocalDate.of(2022, 5, 4), InstallationStatus.Y),
                        tuple(shop, "현관 3층", LocalDate.of(2022, 5, 5), InstallationStatus.Y),
                        tuple(shop, "현관 4층", LocalDate.of(2022, 5, 6), InstallationStatus.Y),
                        tuple(shop, "현관 5층", LocalDate.of(2022, 5, 7), InstallationStatus.Y)
                );
        Assertions.assertThat(result.getContent()).hasSize(5);
    }


}