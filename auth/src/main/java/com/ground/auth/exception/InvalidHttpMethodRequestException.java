package com.ground.auth.exception;

public class InvalidHttpMethodRequestException extends RuntimeException {

    public InvalidHttpMethodRequestException(String requestHttpMethod) {

        super("This " + requestHttpMethod  +
                " request method cannot be processed. Please make a post request.");
    }
}
