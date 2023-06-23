package com.zerobase.reservation.global.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

public class ArgumentException extends DefaultException{

    public ArgumentException(ErrorCode errorCode){
        super(errorCode);
    }

    public ArgumentException(ErrorCode errorCode, String message){
        super(errorCode, message);
    }
}
