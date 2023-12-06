package com.ground.auth.exception;

import com.ground.auth.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 클라이언트(사용자, 브라우저)의 요청에 예외가 발생할 경우 사용하는 예외 클래스입니다.
 * */
@Getter
public class ClientException extends RuntimeException{

    private final ErrorCode errorCode;
    private final HttpStatus responseStatus;
    private final String displayErrorMessage;


    public ClientException(ErrorCode errorCode,String message) {
        this(errorCode, errorCode.getName(), message);
    }

    public ClientException(ErrorCode errorCode,String message, String displayErrorMessage) {
        super(message);
        this.errorCode = errorCode;
        this.responseStatus = errorCode.getResponseStatus();
        this.displayErrorMessage = displayErrorMessage;
    }

}
