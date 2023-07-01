package com.zerobase.reservation.dto.reservation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DeleteReservationDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        String email;

        @Builder
        private Request(String email) {
            this.email = email;
        }
    }
}
