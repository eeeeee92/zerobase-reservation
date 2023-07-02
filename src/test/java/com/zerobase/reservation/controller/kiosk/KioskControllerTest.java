package com.zerobase.reservation.controller.kiosk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.kiosk.InstallationKioskDto;
import com.zerobase.reservation.dto.kiosk.KioskDto;
import com.zerobase.reservation.service.kiosk.KioskService;
import com.zerobase.reservation.type.InstallationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = KioskController.class)
class KioskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KioskService kioskService;


    @Test
    @DisplayName("키오스크 등록")
    @WithMockUser
    public void registration() throws Exception {
        //when //then
        mockMvc.perform(post("/kiosks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("키오스크 설치")
    @WithMockUser
    public void installation() throws Exception {
        //given
        String kioskCode = UUID.randomUUID().toString();

        InstallationKioskDto.Request request = InstallationKioskDto.Request.builder()
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(put("/kiosks/installation/{kioskCode}", kioskCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("키오스크 단건 조회")
    @WithMockUser
    public void read() throws Exception {
        //given
        String kioskCode = UUID.randomUUID().toString();

        String shopName = "상점";
        Shop shop = Shop.builder().name(shopName)
                .build();
        KioskDto kioskDto = KioskDto.builder()
                .shop(shop)
                .kioskCode(UUID.randomUUID().toString())
                .installationLocation("현관")
                .installationYear(LocalDate.of(2022, 5, 23))
                .installationStatus(InstallationStatus.Y)
                .build();

        given(kioskService.getKiosk(any()))
                .willReturn(kioskDto);


        //when //then
        mockMvc.perform(get("/kiosks/{kioskCode}", kioskCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(jsonPath("$.shopName").value(kioskDto.getShop().getName()))
                .andExpect(jsonPath("$.shopCode").value(kioskDto.getShop().getShopCode()))
                .andExpect(jsonPath("$.kioskCode").value(kioskDto.getKioskCode()))
                .andExpect(jsonPath("$.installationLocation").value(kioskDto.getInstallationLocation()))
                .andExpect(jsonPath("$.installationYear").value(kioskDto.getInstallationYear().toString()))
                .andExpect(jsonPath("$.installationStatus").value(kioskDto.getInstallationStatus().getDescription()));

    }

    @Test
    @DisplayName("키오스크 삭제")
    @WithMockUser
    public void delete() throws Exception {
        //given
        String kioskCode = UUID.randomUUID().toString();

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/kiosks/{kioskCode}", kioskCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("키오스크 설치해제")
    @WithMockUser
    public void uninstall() throws Exception {
        //given
        String kioskCode = UUID.randomUUID().toString();

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/kiosks/uninstall/{kioskCode}", kioskCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("검색조건별 키오스크 전체조회")
    @WithMockUser
    public void readAllByCondition() throws Exception {
        //given

        String name = "상점";
        Shop shop = Shop.builder()
                .name(name)
                .build();
        LocalDate installationYear = LocalDate.of(2022, 5, 23);
        InstallationStatus installationStatus = InstallationStatus.Y;
        String installationLocation = "현관1층";
        String kioskCode = UUID.randomUUID().toString();
        List<KioskDto> kiosks = Arrays.asList(KioskDto.builder()
                .shop(shop)
                .kioskCode(kioskCode)
                .installationLocation(installationLocation)
                .installationStatus(installationStatus)
                .installationYear(installationYear)
                .build()
        );
        PageImpl<KioskDto> pageKiosks = new PageImpl<>(kiosks);
        given(kioskService.getKiosksBy(any(), any()))
                .willReturn(pageKiosks);

        //when //then
        mockMvc.perform(get("/kiosks")
                        .param("shopCode", shop.getShopCode())
                        .param("installationStatus", InstallationStatus.Y.name())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(jsonPath("$.content[0].shopName").value(name))
                .andExpect(jsonPath("$.content[0].shopCode").value(shop.getShopCode()))
                .andExpect(jsonPath("$.content[0].kioskCode").value(kioskCode))
                .andExpect(jsonPath("$.content[0].installationLocation").value(installationLocation))
                .andExpect(jsonPath("$.content[0].installationYear").value(installationYear.toString()))
                .andExpect(jsonPath("$.content[0].installationStatus").value(installationStatus.getDescription()));


    }

}