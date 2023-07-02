package com.zerobase.reservation.dto.review;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchConditionReviewDto {
    private String email;
    private String shopCode;

    @Builder
    private SearchConditionReviewDto(String email, String shopCode) {
        this.email = email;
        this.shopCode = shopCode;
    }
}
