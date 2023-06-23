package com.zerobase.reservation.global.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public abstract class DefaultException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String errorMessage;

    public DefaultException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public DefaultException(ErrorCode errorCode, String message){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription() + " [" + message + "]" ;
        log.info("Exception [{}],{}", errorCode, errorMessage);
    }
}
