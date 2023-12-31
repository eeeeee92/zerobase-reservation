package com.zerobase.reservation.service.reservation;

import com.zerobase.reservation.config.AcceptanceTest;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.reservation.ReservationDto;
import com.zerobase.reservation.dto.reservation.SearchConditionReservationDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ConflictException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.ArrivalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.zerobase.reservation.global.exception.ErrorCode.ALREADY_EXIST_RESERVATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        given(shopRepository.findByShopCode(any())).willReturn(Optional.of(shop));
        given(reservationRepository.save(any())).willReturn(reservation);

        //when
        ReservationDto reservationDto = reservationService.create(email, shop.getShopCode(), startDateTime, endDateTime);

        //then
        verify(memberRepository, times(1)).findByEmail(any());
        verify(shopRepository, times(1)).findByShopCode(any());
        verify(reservationRepository, times(1)).existReservationBy(any(), any(), any());
        verify(reservationRepository, times(1)).save(any());


        assertThat(reservationDto.getMember()).isNotNull();
        assertThat(reservationDto.getShop()).isNotNull();
        assertThat(reservationDto).extracting("reservationCode", "startDateTime", "endDateTime", "arrivalStatus")
                .contains(reservation.getReservationCode(), startDateTime, endDateTime, ArrivalStatus.N);

    }

    @Test
    @DisplayName("예약시 종료날짜가 시작날짜 이전이면 예외가 발생한다")
    public void create_mustBeAfterStart() throws Exception {
        //given
        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .build();
        Shop shop = Shop.builder()
                .name("shop1")
                .build();


        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 23, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 23, 11, 59);
        String shopCode = UUID.randomUUID().toString();

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(shopRepository.findByShopCode(any())).willReturn(Optional.of(shop));

        ArgumentException argumentException =
                new ArgumentException(ErrorCode.END_TIME_MUST_BE_AFTER_START_TIME, String.format("start[%s], end[%s]", startDateTime, endDateTime));


        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> reservationService.create(email, shopCode, startDateTime, endDateTime));
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
        String shopCode = UUID.randomUUID().toString();
        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(shopRepository.findByShopCode(any())).willReturn(Optional.of(shop));
        given(reservationRepository.existReservationBy(any(), any(), any())).willReturn(Optional.of(reservation));
        ArgumentException argumentException = new ArgumentException(ErrorCode.ALREADY_EXIST_RESERVATION, String.format("%s or %s", startDateTime, endDateTime));

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> reservationService.create(email, shopCode, startDateTime, endDateTime));
        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());


    }


    @Test
    @DisplayName("검색조건에 따른 예약 전체조회")
    public void getReservations() throws Exception {
        //given
        LocalDateTime startDateTime1 = LocalDateTime.now().plusDays(1);
        LocalDateTime endDateTime2 = LocalDateTime.now().plusDays(2);
        LocalDateTime startDateTime3 = LocalDateTime.now().plusDays(3);
        LocalDateTime endDateTime4 = LocalDateTime.now().plusDays(4);

        Reservation reservation = Reservation.builder()
                .startDateTime(startDateTime1)
                .endDateTime(endDateTime2)
                .build();
        Reservation reservation1 = Reservation.builder()
                .startDateTime(startDateTime3)
                .endDateTime(endDateTime4)
                .build();
        List<Reservation> content = Arrays.asList(reservation, reservation1);
        PageRequest pageable = PageRequest.of(0, 2);
        PageImpl<Reservation> reservations = new PageImpl<>(content, pageable, 2);
        given(reservationRepository.findAllBySearchConditions(any(), any())).willReturn(reservations);
        SearchConditionReservationDto condition = SearchConditionReservationDto.builder()
                .date(LocalDate.now())
                .build();

        //when
        Page<ReservationDto> reservationDtoPage = reservationService.getReservationsByCondition(condition, pageable);

        //then
        assertThat(reservationDtoPage.getContent())
                .extracting("startDateTime", "endDateTime")
                .containsExactlyInAnyOrder(
                        tuple(startDateTime1, endDateTime2),
                        tuple(startDateTime3, endDateTime4)
                );
    }

    @Test
    @DisplayName("예약 상세조회")
    public void getReservation() throws Exception {
        //given
        Member member = Member.builder()
                .build();
        Shop shop = Shop.builder()
                .build();
        LocalDateTime startDateTime = LocalDateTime.of(2022, 05, 23, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 05, 23, 13, 0);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.of(reservation));

        //when
        ReservationDto reservationDto = reservationService.getReservation(reservation.getReservationCode());

        //then
        verify(reservationRepository, times(1)).findByReservationCode(any());
        assertThat(reservationDto)
                .extracting("reservationCode", "startDateTime", "endDateTime", "arrivalStatus")
                .contains(reservation.getReservationCode(), startDateTime, endDateTime, ArrivalStatus.N);
        assertNotNull(reservationDto.getMember());
        assertNotNull(reservationDto.getShop());

    }

    @Test
    @DisplayName("예약상세조회시 예약이 존재하지 않으면 예외가 발생한다")
    public void getReservation_reservationNotFound() throws Exception {

        String reservationCode = UUID.randomUUID().toString();

        //given
        given((reservationRepository.findByReservationCode(any()))).willReturn(Optional.empty());
        ArgumentException argumentException = new ArgumentException(ErrorCode.RESERVATION_NOT_FOUND, reservationCode);

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> reservationService.getReservation(reservationCode));

        verify(reservationRepository, times(1)).findByReservationCode(any());
        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }


    @Test
    @DisplayName("방문 요청")
    public void updateArrival() throws Exception {
        //given
        Shop shop = Shop.builder().build();
        Member member = Member.builder().build();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);
        Reservation reservation = Reservation.builder()
                .shop(shop)
                .member(member)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.of(reservation));
        given(shopRepository.findByShopCode(any()))
                .willReturn(Optional.of(shop));

        //when
        LocalDateTime now = LocalDateTime.now().minusMinutes(5);
        ReservationDto reservationDto =
                reservationService.updateArrival(reservation.getReservationCode(), shop.getShopCode(), now);

        //then
        verify(reservationRepository, times(1)).findByReservationCode(any());
        verify(shopRepository, times(1)).findByShopCode(any());
        assertThat(reservationDto).extracting("reservationCode", "startDateTime", "endDateTime", "arrivalStatus")
                .contains(reservation.getReservationCode(), startDateTime, endDateTime, ArrivalStatus.Y);
        assertNotNull(reservationDto.getMember());
        assertNotNull(reservationDto.getShop());
    }

    @Test
    @DisplayName("방문 요청시 예약이 존재하지 않으면 예외가 발생한다")
    public void updateArrival_reservationNotFound() throws Exception {
        //given
        Shop shop = Shop.builder().build();
        Member member = Member.builder().build();
        Reservation reservation = Reservation.builder()
                .shop(shop)
                .member(member)
                .build();
        ArgumentException argumentException = new ArgumentException(ErrorCode.RESERVATION_NOT_FOUND, reservation.getReservationCode());
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.empty());
        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class,
                () -> reservationService.updateArrival(reservation.getReservationCode(), shop.getShopCode(), LocalDateTime.now()));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("방문 요청시 예약시간 10분전보다 이전에 요청하면 예외가 발생한다")
    public void updateArrival_invalidTimeBefore() throws Exception {
        //given
        Shop shop = Shop.builder().build();
        Member member = Member.builder().build();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);

        LocalDateTime now = LocalDateTime.now().minusMinutes(11);
        Reservation reservation = Reservation.builder()
                .shop(shop)
                .member(member)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        ConflictException conflictException = new ConflictException(ErrorCode.INVALID_TIME, now.toString());
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.of(reservation));
        //when //then

        ConflictException exception = assertThrows(ConflictException.class, () ->
                reservationService.updateArrival(reservation.getReservationCode(), shop.getShopCode(), now));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(conflictException.getErrorCode(), conflictException.getErrorMessage());
    }

    @Test
    @DisplayName("방문 요청시 이용시간 이후에 요청하면 예외가 발생한다")
    public void updateArrival_invalidTimeAfter() throws Exception {
        //given
        Shop shop = Shop.builder().build();
        Member member = Member.builder().build();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);

        LocalDateTime now = LocalDateTime.now().plusHours(1).plusMinutes(1);
        Reservation reservation = Reservation.builder()
                .shop(shop)
                .member(member)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        ConflictException conflictException = new ConflictException(ErrorCode.INVALID_TIME, now.toString());
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.of(reservation));
        //when //then

        ConflictException exception = assertThrows(ConflictException.class, () ->
                reservationService.updateArrival(reservation.getReservationCode(), shop.getShopCode(), now));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(conflictException.getErrorCode(), conflictException.getErrorMessage());
    }

    @Test
    @DisplayName("방문 요청시 이용시간 이후에 요청하면 예외가 발생한다")
    public void updateArrival_shopNotFound() throws Exception {
        //given
        Shop shop = Shop.builder().build();
        Member member = Member.builder().build();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);

        LocalDateTime now = LocalDateTime.now().minusMinutes(5);
        Reservation reservation = Reservation.builder()
                .shop(shop)
                .member(member)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        ArgumentException conflictException = new ArgumentException(ErrorCode.SHOP_NOT_FOUND, shop.getShopCode());
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.of(reservation));
        given(shopRepository.findByShopCode(any())).willReturn(Optional.empty());
        //when //then

        ArgumentException exception = assertThrows(ArgumentException.class, () ->
                reservationService.updateArrival(reservation.getReservationCode(), shop.getShopCode(), now));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(conflictException.getErrorCode(), conflictException.getErrorMessage());
    }

    @Test
    @DisplayName("방문 요청시 예약된 상점과 요청한 상점이 다르면 예외가 발생한다")
    public void updateArrival_unMatchShopCode() throws Exception {
        //given
        Shop shop = Shop.builder()
                .build();
        Shop anotherShop = Shop.builder()
                .build();
        Member member = Member.builder().build();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);

        LocalDateTime now = LocalDateTime.now().minusMinutes(5);
        String parameterShopCode = UUID.randomUUID().toString();
        Reservation reservation = Reservation.builder()
                .shop(shop)
                .member(member)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        ArgumentException conflictException = new ArgumentException(ErrorCode.UN_MATCH_SHOP_CODE, anotherShop.getShopCode());
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.of(reservation));
        given(shopRepository.findByShopCode(any())).willReturn(Optional.of(anotherShop));
        //when //then

        ArgumentException exception = assertThrows(ArgumentException.class, () ->
                reservationService.updateArrival(reservation.getReservationCode(), anotherShop.getShopCode(), now));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(conflictException.getErrorCode(), conflictException.getErrorMessage());
    }

    @Test
    @DisplayName("예약 취소")
    public void delete() throws Exception {
        //given
        String reservationCode = UUID.randomUUID().toString();
        Reservation reservation = Reservation.builder()
                .build();
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.of(reservation));

        //when
        reservationService.delete(reservationCode);

        //then
        verify(reservationRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("예약 취소시 예약이 존재하지 않으면 예외가 발생한다")
    public void delete_reservationNotFound() throws Exception {
        //given

        Reservation reservation = Reservation.builder()
                .build();
        ArgumentException argumentException = new ArgumentException(ErrorCode.RESERVATION_NOT_FOUND, reservation.getReservationCode());
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.empty());
        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class,
                () -> reservationService.delete(reservation.getReservationCode()));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("예약 취소시 이미 방문 완료일 시 예외가 발생한다")
    public void delete_visitedCanNotCancel() throws Exception {
        //given

        String reservationCode = UUID.randomUUID().toString();
        Reservation reservation = Reservation.builder()
                .build();
        reservation.updateArrivalStatus();
        given(reservationRepository.findByReservationCode(any()))
                .willReturn(Optional.of(reservation));


        //when //then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> reservationService.delete(reservationCode));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(ErrorCode.VISITED_CAN_NOT_CANCEL, ErrorCode.VISITED_CAN_NOT_CANCEL.getDescription());
    }


    @Test
    @DisplayName("예약 수정")
    public void update() throws Exception {
        //given
        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .build();
        Shop shop = Shop.builder()
                .name("shop1")
                .build();

        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 22, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 24, 12, 0);

        LocalDateTime updateStartDateTime = LocalDateTime.of(2022, 5, 23, 12, 0);
        LocalDateTime updateEndDateTime = LocalDateTime.of(2022, 5, 26, 12, 0);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        given(reservationRepository.findByReservationCode(any())).willReturn(Optional.of(reservation));

        //when
        ReservationDto reservationDto = reservationService.update(reservation.getReservationCode(), updateStartDateTime, updateEndDateTime);

        //then
        verify(reservationRepository, times(1)).existReservationBy(any(), any(), any());
        assertThat(reservationDto.getMember()).isNotNull();
        assertThat(reservationDto.getShop()).isNotNull();
        assertThat(reservationDto).extracting("reservationCode", "startDateTime", "endDateTime", "arrivalStatus")
                .contains(reservation.getReservationCode(), updateStartDateTime, updateEndDateTime, ArrivalStatus.N);
    }


    @Test
    @DisplayName("예약 수정 시 예약이 존재하지 않으면 예외가 발생한다.")
    public void update_reservationNotFound() throws Exception {
        //given
        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 22, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 24, 12, 0);

        String reservationCode = UUID.randomUUID().toString();

        given(reservationRepository.findByReservationCode(any())).willReturn(Optional.empty());

        ArgumentException argumentException = new ArgumentException(ErrorCode.RESERVATION_NOT_FOUND, reservationCode);

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> reservationService.update(reservationCode, startDateTime, endDateTime));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("예약 수정 시 이미 방문한 예약은 예외가 발생한다.")
    public void update_visitedCanNotChange() throws Exception {
        //given
        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 22, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 24, 12, 0);

        Reservation reservation = Reservation.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        reservation.updateArrivalStatus();
        given(reservationRepository.findByReservationCode(any())).willReturn(Optional.of(reservation));


        //when //then
        ConflictException exception = assertThrows(ConflictException.class, () -> reservationService.update(reservation.getReservationCode(), startDateTime, endDateTime));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(ErrorCode.VISITED_CAN_NOT_CHANGE, ErrorCode.VISITED_CAN_NOT_CHANGE.getDescription());
    }

    @Test
    @DisplayName("예약 수정 시 종료시간이 시작시간 보다 빠르면 예외가 발생한다.")
    public void update_endTimeMustBeAfter() throws Exception {
        //given
        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 24, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 22, 12, 0);

        Reservation reservation = Reservation.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        given(reservationRepository.findByReservationCode(any())).willReturn(Optional.of(reservation));
        ArgumentException argumentException =
                new ArgumentException(ErrorCode.END_TIME_MUST_BE_AFTER_START_TIME, String.format("start[%s], end[%s]", startDateTime, endDateTime));

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> reservationService.update(reservation.getReservationCode(), startDateTime, endDateTime));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("예약 수정 시 해당시간에 이미 예약이 존재하면 예외가 발생한다.")
    public void update_alreadyExistReservation() throws Exception {
        //given
        LocalDateTime startDateTime = LocalDateTime.of(2022, 5, 22, 12, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 5, 24, 12, 0);

        Reservation reservation = Reservation.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();

        given(reservationRepository.findByReservationCode(any())).willReturn(Optional.of(reservation));
        given(reservationRepository.existReservationBy(any(), any(), any())).willReturn(Optional.of(reservation));
        ArgumentException argumentException = new ArgumentException(ALREADY_EXIST_RESERVATION,
                String.format("%s or %s", startDateTime, endDateTime));

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> reservationService.update(reservation.getReservationCode(), startDateTime, endDateTime));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }
}