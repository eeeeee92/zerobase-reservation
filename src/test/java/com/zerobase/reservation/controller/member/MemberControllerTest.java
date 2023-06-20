package com.zerobase.reservation.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.dto.member.DeleteMemberDto;
import com.zerobase.reservation.dto.member.SignupDto;
import com.zerobase.reservation.dto.member.UpdateMemberDto;
import com.zerobase.reservation.service.member.MemberService;
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

import static com.zerobase.reservation.type.Role.USER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("회원가입")
    @WithMockUser
    void signUp() throws Exception {

        //given
        SignupDto signupDto = getDto();
        String json = objectMapper.writeValueAsString(signupDto);


        //when //then
        mockMvc.perform(post("/members/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 수정")
    @WithMockUser
    public void update() throws Exception {
        //given
        String email = "zerobase@naver.com";
        UpdateMemberDto.Request request = UpdateMemberDto.Request.builder()
                .password("1234")
                .nickname("nickname")
                .phoneNumber("01000000000")
                .imageUrl("imageUrl")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(put("/members/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원탈퇴")
    @WithMockUser
    public void delete() throws Exception {
        //given
        String email = "zerobase@naver.com";
        DeleteMemberDto.Request request = DeleteMemberDto.Request.builder()
                .password("1234")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/members/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    private static SignupDto getDto() {
        return SignupDto.builder()
                .email("zerobase@naver.com")
                .password("123123")
                .nickname("제로베이스")
                .phoneNumber("01000000000")
                .role(USER)
                .build();
    }
}