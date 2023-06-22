package com.zerobase.reservation.controller.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.reservation.CreateReservationDto;
import com.zerobase.reservation.dto.reservation.ReservationDto;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("예약")
    @WithMockUser
    public void create() throws Exception {
        //given
        LocalDateTime startDateTime = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endDateTime = LocalDateTime.now().plusSeconds(1);
        CreateReservationDto.Request request = CreateReservationDto.Request.builder()
                .email("zerobase@naver.com")
                .shopId(1L)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(post("/reservation")
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
        String shopId = "1";
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

        ReservationDto reservation = ReservationDto.builder()
                .id(1L)
                .member(member)
                .shop(shop)
                .arrivalStatus(ArrivalStatus.N)
                .startDateTime(startDateTime1)
                .endDateTime(endDateTime2)
                .build();
        ReservationDto reservation1 = ReservationDto.builder()
                .id(2L)
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
        mockMvc.perform(get("/reservation")
                        .param("email", email)
                        .param("shopId", shopId)
                        .param("date", date)
                        .param("page", "0")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].reservationEmail").value(email))
                .andExpect(jsonPath("$.content[0].nickname").value(nickname))
                .andExpect(jsonPath("$.content[0].phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.content[0].shopName").value(shopName))
                .andExpect(jsonPath("$.content[0].latitude").value(latitude))
                .andExpect(jsonPath("$.content[0].longitude").value(longitude))
                .andExpect(jsonPath("$.content[0].arrivalStatus").value(ArrivalStatus.N.getDescription()))
                .andExpect(jsonPath("$.content[0].startDateTime").value(startDateTime1.toString()))
                .andExpect(jsonPath("$.content[0].endDateTime").value(endDateTime2.toString()));

    }
}