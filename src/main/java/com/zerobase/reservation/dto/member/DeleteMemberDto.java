package com.zerobase.reservation.dto.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class DeleteMemberDto {
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class Request {
        @Pattern(regexp = "^(?=.*[A-Za-z0-9])(?=.*[!@#$%^&*()-_=+\\|[{]};:'\",<.>/?]).*$")
        @NotEmpty
        @Length(max = 16, min = 8)
        private String password;

        @Builder
        private Request(String password) {
            this.password = password;
        }
    }
}
