package com.zerobase.reservation.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND("존재하지 않는 회원 입니다."),
    ALREADY_EXIST_EMAIL("이미 존재하는 이메일 입니다."),
    ALREADY_EXIST_NICKNAME("이미 존재하는 닉네임 입니다."),
    INVALID_REQUEST("유효하지 않은 요청 입니다."),
    SHOP_NOT_FOUND("존재하지 않는 상점 입니다."),
    UN_MATCH_PASSWORD("패스워드가 일치하지 않습니다."),
    ALREADY_EXIST_RESERVATION("이미 예약 돼 있는 시간 또는 날짜입니다."),
    END_TIME_MUST_BE_AFTER_START_TIME("종료 날짜는 시작날짜 이후여야 합니다."),
    RESERVATION_NOT_FOUND("존재하지 않는 예약 입니다."),
    KIOSK_NOT_FOUND("존재하지 않는 키오스크 단말기 입니다."),
    INVALID_TIME("아직 방문 요청을 할 수 없거나 방문 예정 시간이 지났습니다."),
    UN_MATCH_SHOP_CODE("예약하신 상점과 방문하신 상점이 일치하지 않습니다."),
    SHOP_NOT_VISITED("방문하지 않은 상점의 리뷰는 작성할 수 없습니다."),
    ALREADY_EXIST_REVIEW("이미 작성 된 리뷰가 존재합니다."),
    REVIEW_NOT_FOUND("리뷰가 존재하지 않습니다"),
    VISITED_CAN_NOT_CANCEL("이미 방문했던 예약은 취소할 수 없습니다."),
    INTERNAL_SERVER_ERROR("서버에 문제가 발생했습니다. 잠시 후 다시 요청 해주세요");


    private final String description;
}
