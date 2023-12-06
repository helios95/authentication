package com.ground.auth.exception;

import com.ground.auth.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ServerException extends RuntimeException{

    private final ErrorCode errorCode;
    private final HttpStatus responseStatus;
    private final String displayErrorMessage;


    public ServerException(ErrorCode errorCode, String message) {
        this(errorCode, errorCode.getName(), message);
    }
    public ServerException(ErrorCode errorCode, String message,
                           String displayErrorMessage) {
        super(message);
        this.errorCode = errorCode;
        this.responseStatus = errorCode.getResponseStatus();
        this.displayErrorMessage = displayErrorMessage;

    }

}
