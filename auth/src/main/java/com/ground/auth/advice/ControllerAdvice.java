package com.ground.auth.advice;


import com.ground.auth.exception.InvalidAuthorizationHeaderException;
import com.ground.auth.exception.InvalidTokenException;
import com.yalooStore.common_utils.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 예외 처리에 사용하는 advice 클래스
 * */

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public ResponseEntity<ResponseDto<Object>> handlerInvalidAuthorizationException(Exception e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ResponseDto.builder()
                        .success(false)
                        .status(HttpStatus.UNAUTHORIZED)
                        .errorMessages(List.of(e.getMessage()))
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handlerException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseDto.builder()
                        .success(false)
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .errorMessages(List.of(e.getMessage()))
                        .build()
        );
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResponseDto<Object>> handlerInvalidTokenException(Exception e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDto.builder()
                        .success(false)
                        .status(HttpStatus.FORBIDDEN)
                        .errorMessages(List.of(e.getMessage()))
                        .build()
        );
    }
}
