package com.zerobase.reservation.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.controller.shop.ShopController;
import com.zerobase.reservation.dto.shop.CreateShopDto;
import com.zerobase.reservation.dto.shop.ShopDto;
import com.zerobase.reservation.service.shop.ShopService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.zerobase.reservation.global.exception.ErrorCode.INVALID_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShopController.class)
public class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShopService shopService;

    @Test
    @DisplayName("상점 등록")
    @WithMockUser
    public void create() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String name = "샵1";
        double latitude = 12.0;
        double longitude = 13.0;
        CreateShopDto.Request request = getRequest(email, name, latitude, longitude);
        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())

                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상점 등록시 이메일 형식을 지키지 않으면 예외형식으로 응답한다")
    @WithMockUser
    public void create_invalidRequest() throws Exception {
        //given
        String email = "아무거나";
        String name = "샵1";
        double latitude = 12.0;
        double longitude = 13.0;
        CreateShopDto.Request request = getRequest(email, name, latitude, longitude);
        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())

                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errorCode").value(INVALID_REQUEST.name()));
    }

    @Test
    @DisplayName("상점 등록시 상점이름이 보내지 않으면 예외형식으로 응답한다")
    @WithMockUser
    public void create_null_name() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String name = null;
        double latitude = 12.0;
        double longitude = 13.0;
        CreateShopDto.Request request = getRequest(email, name, latitude, longitude);
        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())

                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errorCode").value(INVALID_REQUEST.name()));
    }

    @Test
    @DisplayName("상점 상세조회")
    @WithMockUser
    public void read() throws Exception {
        //given
        Long shopId = 1L;
        String name = "샵1";
        double rating = 5.0;
        double latitude = 12.0;
        double longitude = 12.1;
        given(shopService.getShop(any())).willReturn(ShopDto.builder()
                        .id(shopId)
                        .name(name)
                        .rating(rating)
                        .latitude(latitude)
                        .longitude(longitude)
                .build());

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}",shopId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shopId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.rating").value(rating))
                .andExpect(jsonPath("$.latitude").value(latitude))
                .andExpect(jsonPath("$.longitude").value(longitude));
    }

    private static CreateShopDto.Request getRequest(String email, String name, double latitude, double longitude) {
        return CreateShopDto.Request.builder()
                .email(email)
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }


}
