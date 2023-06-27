package com.zerobase.reservation.dto.member;

import com.zerobase.reservation.type.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupDto {
    @Email
    @NotNull
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z0-9])(?=.*[!@#$%^&*()-_=+\\|[{]};:'\",<.>/?]).*$")
    @NotEmpty
    @Length(max = 16, min = 8)
    private String password;

    @NotEmpty
    @Length(min = 2, max = 15)
    private String nickname;

    @NotEmpty
    @Pattern(regexp = "^010\\d{8}$")
    private String phoneNumber;

    @NotNull
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
