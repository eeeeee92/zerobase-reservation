package com.zerobase.reservation.controller.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.review.CreateReviewDto;
import com.zerobase.reservation.dto.review.DeleteReviewDto;
import com.zerobase.reservation.dto.review.ReviewDto;
import com.zerobase.reservation.dto.review.UpdateReviewDto;
import com.zerobase.reservation.service.review.ReviewService;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    @DisplayName("리뷰 상세조회")
    @WithMockUser
    public void read() throws Exception {
        //given

        String email = "zerobase@naver.com";
        String nickname = "zerobase";
        String memberImageUrl = "imageUrl";
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .imageUrl(memberImageUrl)
                .build();

        Shop shop = Shop.builder()
                .name("shop")
                .build();

        String shopCode = shop.getShopCode();
        String shopName = shop.getName();
        String reviewCode = UUID.randomUUID().toString();


        Integer rating = 1;
        String content = "content";
        String reviewImageUrl = "imageUrl";

        ReviewDto reviewDto = ReviewDto.builder()
                .member(member)
                .shop(shop)
                .reviewCode(reviewCode)
                .rating(1)
                .content(content)
                .imageUrl(reviewImageUrl)
                .build();

        given(reviewService.getReview(any()))
                .willReturn(reviewDto);


        //when //then
        mockMvc.perform(get("/reviews/{reviewCode}", reviewCode)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.memberImageUrl").value(memberImageUrl))
                .andExpect(jsonPath("$.shopCode").value(shopCode))
                .andExpect(jsonPath("$.shopName").value(shopName))
                .andExpect(jsonPath("$.reviewCode").value(reviewCode))
                .andExpect(jsonPath("$.rating").value(rating))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.reviewImageUrl").value(reviewImageUrl));
    }

    @Test
    @DisplayName("리뷰 삭제")
    @WithMockUser
    public void delete() throws Exception {
        //given
        String reviewCode = UUID.randomUUID().toString();
        DeleteReviewDto.Request request = DeleteReviewDto.Request.builder()
                .email("zerobase@naver.com")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/{reviewCode}", reviewCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("리뷰 수정")
    @WithMockUser
    public void update() throws Exception {
        //given
        String reviewCode = UUID.randomUUID().toString();
        UpdateReviewDto.Request request = UpdateReviewDto.Request.builder()
                .email("zerobase@naver.com")
                .content("content")
                .imageUrl("imageUrl")
                .rating(1)
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.put("/reviews/{reviewCode}", reviewCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("검색조건별 리뷰 전체조회")
    @WithMockUser
    public void readAllByConditions() throws Exception {
        //given
        String email = "zerobase@naver.com";

        String memberImageUrl = "imageUrl";
        String nickname = "닉네임";
        Member member = Member.builder()
                .email(email)
                .imageUrl(memberImageUrl)
                .nickname(nickname)
                .build();
        String name = "상점";
        Shop shop = Shop.builder()
                .name(name)
                .build();
        String reviewCode = UUID.randomUUID().toString();
        int rating = 1;
        String content = "content";
        String reviewImageUrl = "imageUrl";
        List<ReviewDto> reviews = Arrays.asList(ReviewDto.builder()
                .member(member)
                .shop(shop)
                .reservation(Reservation.builder().build())
                .reviewCode(reviewCode)
                .rating(rating)
                .content(content)
                .imageUrl(reviewImageUrl)
                .build()
        );

        PageImpl<ReviewDto> pageReviews = new PageImpl<>(reviews);
        given(reviewService.getReviewsBy(any(), any()))
                .willReturn(pageReviews);


        //when //then
        mockMvc.perform(get("/reviews")
                        .param("shopCode", shop.getShopCode())
                        .param("email", email)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value(email))
                .andExpect(jsonPath("$.content[0].nickname").value(nickname))
                .andExpect(jsonPath("$.content[0].memberImageUrl").value(memberImageUrl))
                .andExpect(jsonPath("$.content[0].shopCode").value(shop.getShopCode()))
                .andExpect(jsonPath("$.content[0].shopName").value(name))
                .andExpect(jsonPath("$.content[0].reviewCode").value(reviewCode))
                .andExpect(jsonPath("$.content[0].rating").value(rating))
                .andExpect(jsonPath("$.content[0].content").value(content))
                .andExpect(jsonPath("$.content[0].reviewImageUrl").value(reviewImageUrl));

    }

}