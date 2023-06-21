package com.zerobase.reservation.domain.reservation;

import com.zerobase.reservation.type.ArrivalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

    @Test
    @DisplayName("처음 예약을 생성하면 arrivalStatus 는 N 이 돼야 한다")
    public void arrivalStatus() throws Exception {
        //given
        Reservation reservation = Reservation.builder()
                .build();

        //when //then
        assertThat(reservation.getArrivalStatus()).isEqualTo(ArrivalStatus.N);
    }

}