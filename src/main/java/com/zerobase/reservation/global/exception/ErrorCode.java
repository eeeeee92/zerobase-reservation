package com.zerobase.reservation.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND("존재하지 않는 회원 입니다."),
    ALREADY_EXIST_EMAIL("이미 존재하는 이메일 입니다."),
    ALREADY_EXIST_NICKNAME("이미 존재하는 닉네임 입니다."),
    INVALID_REQUEST("유효하지 않은 요청 입니다"),
    SHOP_NOT_FOUND("존재하지 않는 상점 입니다."),
    UN_MATCH_PASSWORD("패스워드가 일치하지 않습니다");
    private final String description;
}
