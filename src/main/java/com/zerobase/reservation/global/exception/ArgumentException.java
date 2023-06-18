package com.zerobase.reservation.global.exception;

import lombok.Getter;

@Getter
public class ArgumentException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String errorMessage;

    public ArgumentException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public ArgumentException(ErrorCode errorCode, String message){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription() + " [" + message + "]" ;
    }

}
