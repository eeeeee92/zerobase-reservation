package com.zerobase.reservation.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private final int code;
    private final HttpStatus status;
    private final ErrorCode errorCode;
    private final String ErrorMessage;

    @Builder
    private ErrorResponse(int code, HttpStatus status, ErrorCode errorCode, String errorMessage) {
        this.code = code;
        this.status = status;
        this.errorCode = errorCode;
        ErrorMessage = errorMessage;
    }
}
