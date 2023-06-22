package com.zerobase.reservation.dto.reservation;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchConditionReservationDto {
    private Long shopId;
    private String email;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;


    @Builder
    private SearchConditionReservationDto(Long shopId, String email, LocalDate date) {
        this.shopId = shopId;
        this.email = email;
        this.date = date;
    }
}
