package com.zerobase.reservation.dto.member;


import com.zerobase.reservation.type.SocialType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberInfoDetail {


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
        private String email;
        private String password;
        private String nickname;
        private String imageUrl;
        private String phoneNumber;
        private SocialType socialType;

        @Builder
        private Response(String email, String password, String nickname, String imageUrl, String phoneNumber, SocialType socialType) {
            this.email = email;
            this.password = password;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.phoneNumber = phoneNumber;
            this.socialType = socialType;
        }


        public static Response of(MemberDto memberDto) {
            return Response.builder()
                    .email(memberDto.getEmail())
                    .password(memberDto.getPassword())
                    .nickname(memberDto.getNickname())
                    .imageUrl(memberDto.getImageUrl())
                    .phoneNumber(memberDto.getPhoneNumber())
                    .socialType(memberDto.getSocialType())
                    .build();
        }
    }


}
