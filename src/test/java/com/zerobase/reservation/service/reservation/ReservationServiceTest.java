package com.zerobase.reservation.service.reservation;

import com.zerobase.reservation.config.AcceptanceTest;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.reservation.ReservationDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.ArrivalStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@AcceptanceTest
class ReservationServiceTest {

    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private ShopRepository shopRepository;
    @MockBean
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    @Test
    @DisplayName("예약")
    public void create() throws Exception {
        //given
        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .build();
        Shop shop = Shop.builder()
                .name("shop1")
                .build();

        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 23, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 26, 12, 0);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(shopRepository.findById(any())).willReturn(Optional.of(shop));
        given(reservationRepository.save(any())).willReturn(reservation);

        //when
        ReservationDto reservationDto = reservationService.create(email, 1L, startDateTime, endDateTime);

        //then
        assertThat(reservationDto.getMember()).isNotNull();
        assertThat(reservationDto.getShop()).isNotNull();
        assertThat(reservationDto).extracting("startDateTime", "endDateTime", "arrivalStatus")
                .contains(startDateTime, endDateTime, ArrivalStatus.N);

    }

    @Test
    @DisplayName("예약시 종료날짜가 시작날짜 이전이면 예외가 발생한다")
    public void create_mustBeAfterStart() throws Exception {
        //given
        String email = "zerobase@naver.com";


        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 23, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 23, 11, 59);


        ArgumentException argumentException =
                new ArgumentException(ErrorCode.END_TIME_MUST_BE_AFTER_START_TIME, String.format("start[%s], end[%s]", startDateTime, endDateTime));

        //when //then
        ArgumentException exception = Assertions.assertThrows(ArgumentException.class, () -> reservationService.create(email, 1L, startDateTime, endDateTime));
        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("예약시 해당 시간 또는 날짜에 이미 예약이 존재하면 예외가 발생한다")
    public void create_alreadyExistReservation() throws Exception {
        //given
        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .build();
        Shop shop = Shop.builder()
                .name("shop1")
                .build();

        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 23, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 26, 12, 0);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(shopRepository.findById(any())).willReturn(Optional.of(shop));
        given(reservationRepository.confirmReservation(startDateTime, endDateTime)).willReturn(Optional.of(reservation));
        ArgumentException argumentException = new ArgumentException(ErrorCode.ALREADY_EXIST_RESERVATION, String.format("%s or %s", startDateTime, endDateTime));

        //when //then
        ArgumentException exception = Assertions.assertThrows(ArgumentException.class, () -> reservationService.create(email, 1L, startDateTime, endDateTime));
        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());


    }
}