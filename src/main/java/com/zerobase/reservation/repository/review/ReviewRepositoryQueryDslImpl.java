package com.zerobase.reservation.repository.review;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.dto.review.SearchConditionReviewDto;
import com.zerobase.reservation.repository.QueryDsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import static com.zerobase.reservation.domain.review.QReview.review;

public class ReviewRepositoryQueryDslImpl extends QueryDsl4RepositorySupport implements ReviewRepositoryQueryDsl {
    public ReviewRepositoryQueryDslImpl() {
        super(Review.class);
    }

    @Override
    public Page<Review> findAllBySearchConditions(SearchConditionReviewDto searchCondition, Pageable pageable) {
        return applyPagination(pageable, (query) -> query
                        .selectFrom(review)
                        .leftJoin(review.member).fetchJoin()
                        .leftJoin(review.shop).fetchJoin()
                        .leftJoin(review.reservation).fetchJoin()
                        .where(
                                shopCodeEq(searchCondition.getShopCode()),
                                emailEq(searchCondition.getEmail())
                        ),
                countQuery -> countQuery
                        .selectFrom(review)
                        .leftJoin(review.member).fetchJoin()
                        .leftJoin(review.shop).fetchJoin()
                        .leftJoin(review.reservation).fetchJoin()
                        .where(
                                shopCodeEq(searchCondition.getShopCode()),
                                emailEq(searchCondition.getEmail())
                        )
        );
    }


    /**
     * 상점별 검색조건
     */
    private BooleanExpression shopCodeEq(String shopCode) {
        return !StringUtils.hasText(shopCode) ? null : review.shop.shopCode.eq(shopCode);
    }

    /**
     * 회원별 검색조건
     */
    private BooleanExpression emailEq(String email) {
        return !StringUtils.hasText(email) ? null : review.member.email.eq(email);
    }

}
