package com.zerobase.reservation.controller.review;

import com.zerobase.reservation.dto.review.*;
import com.zerobase.reservation.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 등록
     */
    @PostMapping
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) and (hasRole('USER')))")
    public ResponseEntity<?> create(@Valid @RequestBody CreateReviewDto.Request request) {

        reviewService.create(request.getEmail(), request.getShopCode(),
                request.getReservationCode(), request.getRating(),
                request.getContent(), request.getImageUrl()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰 단건조회
     */
    @GetMapping("/{reviewCode}")
    public ResponseEntity<ReviewInfoDetailDto.Response> read(@PathVariable String reviewCode) {
        return ResponseEntity.ok(
                ReviewInfoDetailDto.Response.of(reviewService.getReview(reviewCode))
        );
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/{reviewCode}")
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) and (hasRole('USER')))")
    public ResponseEntity<?> delete(@PathVariable String reviewCode,
                                    @RequestBody DeleteReviewDto.Request request) {
        reviewService.delete(reviewCode);
        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰 수정
     */
    @PutMapping("/{reviewCode}")
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) and (hasRole('USER')))")
    public ResponseEntity<?> update(@PathVariable String reviewCode,
                                    @RequestBody UpdateReviewDto.Request request) {
        reviewService.update(reviewCode, request.getContent(), request.getRating(), request.getImageUrl());
        return ResponseEntity.ok().build();
    }

    /**
     * 검색조건별 리뷰전체조회
     */
    @GetMapping
    public ResponseEntity<Page<ReviewInfoDto.Response>> readAllByConditions(
            SearchConditionReviewDto searchConditionReviewDto,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                reviewService.getReviewsBy(searchConditionReviewDto, pageable)
                        .map(ReviewInfoDto.Response::of)
        );
    }

}
