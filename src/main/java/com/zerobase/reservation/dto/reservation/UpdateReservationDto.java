package com.zerobase.reservation.dto.reservation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UpdateReservationDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        private String email;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;

        @Builder
        private Request(String email, LocalDateTime startDateTime, LocalDateTime endDateTime) {
            this.email = email;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }
    }
}
