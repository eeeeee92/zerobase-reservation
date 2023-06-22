package com.zerobase.reservation.dto.reservation;

import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.type.ArrivalStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReservationInfoDto {


    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Response{
        private Long id;
        private String reservationEmail;
        private String nickname;
        private String phoneNumber;

        private Long shopId;
        private String shopName;
        private Double latitude;
        private Double longitude;

        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String arrivalStatus;


        @Builder
        private Response(Long id, String reservationEmail, String nickname, String phoneNumber, Long shopId, String shopName, Double latitude, Double longitude, LocalDateTime startDateTime, LocalDateTime endDateTime, String arrivalStatus) {
            this.id = id;
            this.reservationEmail = reservationEmail;
            this.nickname = nickname;
            this.phoneNumber = phoneNumber;
            this.shopId = shopId;
            this.shopName = shopName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.arrivalStatus = arrivalStatus;
        }

        public static Response of(ReservationDto reservationDto){
            return Response.builder()
                    .id(reservationDto.getId())
                    .reservationEmail(reservationDto.getMember().getEmail())
                    .nickname(reservationDto.getMember().getNickname())
                    .phoneNumber(reservationDto.getMember().getPhoneNumber())
                    .shopId(reservationDto.getShop().getId())
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
