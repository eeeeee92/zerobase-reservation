package com.zerobase.reservation.global.exception;

public class ConflictException extends DefaultException{
    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
