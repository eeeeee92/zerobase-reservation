package com.zerobase.reservation.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND("존재하지 않는 회원입니다."),
    ALREADY_EXIST_EMAIL("이미 존재하는 이메일 입니다."),
    ALREADY_EXIST_NICKNAME("이미 존재하는 닉네임 입니다."),
    INVALID_REQUEST("유효하지 않은 요청입니다")
    ;
    private final String description;
}
