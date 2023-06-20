package com.zerobase.reservation.dto.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DeleteMemberDto {
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {
        private String password;

        @Builder
        private Request(String password) {
            this.password = password;
        }
    }
}
