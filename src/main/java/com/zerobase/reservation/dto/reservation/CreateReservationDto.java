package com.zerobase.reservation.dto.reservation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateReservationDto {

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {
        @Email
        @NotNull
        private String email;
        @NotEmpty
        private String shopCode;

        @Future
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startDateTime;
        @Future
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endDateTime;

        @Builder
        private Request(String email, String shopCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {
            this.email = email;
            this.shopCode = shopCode;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }
    }
}
