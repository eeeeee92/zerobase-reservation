package com.zerobase.reservation.controller.review;

import com.zerobase.reservation.dto.review.CreateReviewDto;
import com.zerobase.reservation.dto.review.ReviewInfoDetailDto;
import com.zerobase.reservation.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping
    @PreAuthorize("isAuthenticated() and ((request.email == principal.username) and (hasRole('USER')))")
    public ResponseEntity<?> create(@Valid @RequestBody CreateReviewDto.Request request) {

        reviewService.create(request.getEmail(), request.getShopCode(),
                request.getReservationCode(), request.getRating(),
                request.getContent(), request.getImageUrl()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{reviewCode}")
    public ResponseEntity<ReviewInfoDetailDto.Response> read(@PathVariable String reviewCode) {
        return ResponseEntity.ok(
                ReviewInfoDetailDto.Response.of(reviewService.read(reviewCode))
        );
    }

}
