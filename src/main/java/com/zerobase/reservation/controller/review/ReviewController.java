package com.zerobase.reservation.controller.review;

import com.zerobase.reservation.dto.review.CreateReviewDto;
import com.zerobase.reservation.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateReviewDto.Request request) {

        reviewService.create(request.getEmail(), request.getShopCode(),
                request.getReservationCode(), request.getRating(),
                request.getContent(), request.getImageUrl()
        );
        return ResponseEntity.ok().build();
    }
}
