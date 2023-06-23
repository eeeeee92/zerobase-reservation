package com.zerobase.reservation.global.exception;

public class ServerErrorException extends DefaultException{

    public ServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ServerErrorException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
