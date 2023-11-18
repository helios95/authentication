package com.ground.auth.exception;

public class InvalidAuthorizationHeaderException extends RuntimeException {
    public InvalidAuthorizationHeaderException() {
        super("this is invalid authorization header!!");
    }
}
