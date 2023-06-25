package com.zerobase.reservation.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.review.CreateReviewDto;
import com.zerobase.reservation.dto.review.ReviewDto;
import com.zerobase.reservation.service.review.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 등록")
    @WithMockUser
    public void create() throws Exception {

        //given
        CreateReviewDto.Request request = CreateReviewDto.Request.builder()
                .email("zerobase@naver.com")
                .shopCode(UUID.randomUUID().toString())
                .reservationCode(UUID.randomUUID().toString())
                .rating(1)
                .content("content")
                .imageUrl("imageUrl")
                .build();
        String json = objectMapper.writeValueAsString(request);

        String reviewCode = Review.builder().build().getReviewCode();
        ReviewDto reviewDto = ReviewDto.builder()
                .member(Member.builder().build())
                .shop(Shop.builder().build())
                .reviewCode(reviewCode)
                .rating(1)
                .content("content")
                .imageUrl("imageUrl")
                .build();

        given(reviewService.create(any(), any(), any(), any(), any(), any()))
                .willReturn(reviewDto);

        //when //then
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

}