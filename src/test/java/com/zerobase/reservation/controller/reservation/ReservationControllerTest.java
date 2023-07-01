package com.zerobase.reservation.controller.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.kiosk.KioskDto;
import com.zerobase.reservation.dto.reservation.CreateReservationDto;
import com.zerobase.reservation.dto.reservation.DeleteReservationDto;
import com.zerobase.reservation.dto.reservation.ReservationDto;
import com.zerobase.reservation.dto.reservation.UpdateReservationArrivalDto;
import com.zerobase.reservation.service.kiosk.KioskService;
import com.zerobase.reservation.service.reservation.ReservationService;
import com.zerobase.reservation.type.ArrivalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private KioskService kioskService;

    @Test
    @DisplayName("예약")
    @WithMockUser
    public void create() throws Exception {
        //given
        LocalDateTime startDateTime = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusSeconds(1);
        String shopCode = UUID.randomUUID().toString();
        CreateReservationDto.Request request = CreateReservationDto.Request.builder()
                .email("zerobase@naver.com")
                .shopCode(shopCode)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("예약전체조회_조건별검색")
    @WithMockUser
    public void readAllByCondition() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String shopCode = UUID.randomUUID().toString();
        String date = "2022-05-23";

        LocalDateTime startDateTime1 = LocalDateTime.now().plusDays(1);
        LocalDateTime endDateTime2 = LocalDateTime.now().plusDays(2);
        LocalDateTime startDateTime3 = LocalDateTime.now().plusDays(3);
        LocalDateTime endDateTime4 = LocalDateTime.now().plusDays(4);

        String nickname = "zerobase";
        String phoneNumber = "01000000000";
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .build();

        String shopName = "shop";
        double latitude = 12.0;
        double longitude = 12.0;
        Shop shop = Shop.builder()
                .name(shopName)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        String reservationCode1 = UUID.randomUUID().toString();
        String reservationCode2 = UUID.randomUUID().toString();

        ReservationDto reservation = ReservationDto.builder()
                .reservationCode(reservationCode1)
                .member(member)
                .shop(shop)
                .arrivalStatus(ArrivalStatus.N)
                .startDateTime(startDateTime1)
                .endDateTime(endDateTime2)
                .build();
        ReservationDto reservation1 = ReservationDto.builder()
                .reservationCode(reservationCode2)
                .member(member)
                .shop(shop)
                .arrivalStatus(ArrivalStatus.N)
                .startDateTime(startDateTime3)
                .endDateTime(endDateTime4)
                .build();
        List<ReservationDto> content = Arrays.asList(reservation, reservation1);
        PageRequest pageable = PageRequest.of(0, 2);
        PageImpl<ReservationDto> reservations = new PageImpl<>(content, pageable, 2);

        given(reservationService.getReservationsByCondition(any(), any())).willReturn(reservations);

        //when //then
        mockMvc.perform(get("/reservations")
                        .param("email", email)
                        .param("shopCode", shopCode)
                        .param("date", date)
                        .param("page", "0")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reservationCode").value(reservation.getReservationCode()))
                .andExpect(jsonPath("$.content[0].reservationEmail").value(email))
                .andExpect(jsonPath("$.content[0].nickname").value(nickname))
                .andExpect(jsonPath("$.content[0].phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.content[0].shopCode").value(shop.getShopCode()))
                .andExpect(jsonPath("$.content[0].shopName").value(shopName))
                .andExpect(jsonPath("$.content[0].latitude").value(latitude))
                .andExpect(jsonPath("$.content[0].longitude").value(longitude))
                .andExpect(jsonPath("$.content[0].arrivalStatus").value(ArrivalStatus.N.getDescription()))
                .andExpect(jsonPath("$.content[0].startDateTime").value(startDateTime1.toString()))
                .andExpect(jsonPath("$.content[0].endDateTime").value(endDateTime2.toString()));

    }


    @Test
    @DisplayName("예약 상세조회")
    @WithMockUser
    public void read() throws Exception {
        //given

        String email = "zerobase@naver.com";
        String nickname = "zerobase";
        String phoneNumber = "01000000000";
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .build();
        String shopName = "shop";
        double latitude = 12.0;
        double longitude = 12.1;
        Shop shop = Shop.builder()
                .name(shopName)
                .latitude(latitude)
                .longitude(longitude)
                .build();

        String reservationCode = UUID.randomUUID().toString();
        LocalDateTime startDateTime = LocalDateTime.of(2022, 05, 23, 12, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2022, 05, 23, 13, 0, 0, 0);
        ReservationDto reservationDto = ReservationDto.builder()
                .reservationCode(reservationCode)
                .member(member)
                .shop(shop)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .arrivalStatus(ArrivalStatus.N)
                .build();

        given(reservationService.getReservation(any()))
                .willReturn(reservationDto);

        //when //then
        mockMvc.perform(get("/reservations/{reservationCode}", reservationCode)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationCode").value(reservationCode))
                .andExpect(jsonPath("$.reservationEmail").value(email))
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.shopCode").value(shop.getShopCode()))
                .andExpect(jsonPath("$.shopName").value(shopName))
                .andExpect(jsonPath("$.latitude").value(latitude))
                .andExpect(jsonPath("$.longitude").value(longitude))
                .andExpect(jsonPath("$.startDateTime").value(dateFormat(startDateTime)))
                .andExpect(jsonPath("$.endDateTime").value(dateFormat(endDateTime)))
                .andExpect(jsonPath("$.arrivalStatus").value(ArrivalStatus.N.getDescription()));

    }


    @Test
    @DisplayName("방문 요청")
    @WithMockUser
    public void arrival() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String kioskCode = UUID.randomUUID().toString();

        Reservation reservation = Reservation.builder().build();
        UpdateReservationArrivalDto.Request request = UpdateReservationArrivalDto.Request.builder()
                .email(email)
                .kioskCode(kioskCode)
                .build();
        String json = objectMapper.writeValueAsString(request);

        Shop shop = Shop.builder().build();

        KioskDto kiosk = KioskDto.builder()
                .shop(shop)
                .build();

        given(kioskService.getKiosk(any())).willReturn(kiosk);

        //when //then
        mockMvc.perform(put("/reservations/{reservationCode}/arrival", reservation.getReservationCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("예약 취소")
    @WithMockUser
    public void delete() throws Exception {
        //given
        String reservationCode = UUID.randomUUID().toString();
        DeleteReservationDto.Request request = DeleteReservationDto.Request.builder()
                .email("zerobase@naver.com")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/reservations/{reservationCode}", reservationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    private static String dateFormat(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").format(dateTime);
    }
}