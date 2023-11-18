package com.ground.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class UserPasswordNotMatchesException extends AuthenticationException {

    public UserPasswordNotMatchesException(String message) {
        super(message);
    }
}
