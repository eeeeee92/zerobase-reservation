package com.zerobase.reservation.dto.reservation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReservationInfoDto {


    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Response {
        private String reservationCode;
        private String reservationEmail;
        private String nickname;
        private String phoneNumber;

        private String shopCode;
        private String shopName;
        private Double latitude;
        private Double longitude;

        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String arrivalStatus;


        @Builder
        private Response(String reservationCode, String reservationEmail, String nickname, String phoneNumber, String shopCode, String shopName, Double latitude, Double longitude, LocalDateTime startDateTime, LocalDateTime endDateTime, String arrivalStatus) {
            this.reservationCode = reservationCode;
            this.reservationEmail = reservationEmail;
            this.nickname = nickname;
            this.phoneNumber = phoneNumber;
            this.shopCode = shopCode;
            this.shopName = shopName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.arrivalStatus = arrivalStatus;
        }

        public static Response of(ReservationDto reservationDto) {
            return Response.builder()
                    .reservationCode(reservationDto.getReservationCode())
                    .reservationEmail(reservationDto.getMember().getEmail())
                    .nickname(reservationDto.getMember().getNickname())
                    .phoneNumber(reservationDto.getMember().getPhoneNumber())
                    .shopCode(reservationDto.getShop().getShopCode())
                    .shopName(reservationDto.getShop().getName())
                    .latitude(reservationDto.getShop().getLatitude())
                    .longitude(reservationDto.getShop().getLongitude())
                    .startDateTime(reservationDto.getStartDateTime())
                    .endDateTime(reservationDto.getEndDateTime())
                    .arrivalStatus(reservationDto.getArrivalStatus().getDescription())
                    .build();
        }
    }
}
