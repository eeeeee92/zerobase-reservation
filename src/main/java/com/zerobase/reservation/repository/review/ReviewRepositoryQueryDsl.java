package com.zerobase.reservation.repository.review;

import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.dto.review.SearchConditionReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepositoryQueryDsl {
    Page<Review> findAllBySearchConditions(SearchConditionReviewDto searchConditions, Pageable pageable);
}
