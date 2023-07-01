package com.zerobase.reservation.dto.reservation;

import lombok.*;

public class UpdateReservationArrivalDto {

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {

        private String email;
        private String kioskCode;

        @Builder
        private Request(String email, String kioskCode) {
            this.email = email;
            this.kioskCode = kioskCode;
        }
    }
}