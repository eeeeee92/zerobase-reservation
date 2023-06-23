package com.zerobase.reservation.dto.member;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.type.Role;
import com.zerobase.reservation.type.SocialType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDto {

    private String email;
    private String password;
    private String nickname;
    private String imageUrl;
    private String phoneNumber;
    private Role role;
    private SocialType socialType;
    private String socialId;
    private String refreshToken;

    @Builder
    private MemberDto(String email, String password, String nickname, String imageUrl, String phoneNumber, Role role, SocialType socialType, String socialId, String refreshToken) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
    }

    public static MemberDto of(Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .phoneNumber(member.getPhoneNumber())
                .role(member.getRole())
                .socialType(member.getSocialType())
                .role(member.getRole())
                .socialType(member.getSocialType())
                .socialId(member.getSocialId())
                .refreshToken(member.getRefreshToken())
                .build();
    }
}
