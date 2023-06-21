package com.zerobase.reservation.dto.reservation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import java.time.LocalDateTime;

public class CreateReservationDto {

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {
        @Email
        private String email;
        private Long shopId;

        @Future
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startDateTime;
        @Future
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endDateTime;

        @Builder
        private Request(String email, Long shopId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
            this.email = email;
            this.shopId = shopId;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }
    }
}
