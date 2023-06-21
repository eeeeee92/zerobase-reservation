package com.zerobase.reservation.controller.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.dto.reservation.CreateReservationDto;
import com.zerobase.reservation.service.reservation.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
}