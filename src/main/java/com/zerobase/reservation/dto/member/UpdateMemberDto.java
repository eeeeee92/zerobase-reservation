package com.zerobase.reservation.dto.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UpdateMemberDto {


    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {
        private String password;
        private String nickname;
        private String imageUrl;
        private String phoneNumber;

        @Builder
        private Request(String password, String nickname, String imageUrl, String phoneNumber) {
            this.password = password;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.phoneNumber = phoneNumber;
        }
    }
}
