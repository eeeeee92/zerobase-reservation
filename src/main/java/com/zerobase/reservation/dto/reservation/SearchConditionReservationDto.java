package com.zerobase.reservation.dto.reservation;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchConditionReservationDto {
    private String shopCode;
    @Email
    private String email;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;


    @Builder
    private SearchConditionReservationDto(String shopCode, String email, LocalDate date) {
        this.shopCode = shopCode;
        this.email = email;
        this.date = date;
    }
}
