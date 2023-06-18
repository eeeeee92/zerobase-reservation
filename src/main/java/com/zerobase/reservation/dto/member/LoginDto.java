package com.zerobase.reservation.dto.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class LoginDto {


    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Response {
        private String email;
        private String nickname;
        private String imageUrl;

        @Builder
        private Response(String email, String nickname, String imageUrl) {
            this.email = email;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
        }
    }
}
