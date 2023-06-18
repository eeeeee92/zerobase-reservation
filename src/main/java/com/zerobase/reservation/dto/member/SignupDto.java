package com.zerobase.reservation.dto.member;

import com.zerobase.reservation.type.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupDto {

    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private Role role;

    @Builder
    private SignupDto(String email, String password, String nickname, String phoneNumber, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}
