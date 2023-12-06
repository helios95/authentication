package com.ground.auth.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 *
 * 발생하는 예외들을 공통적으로 처리하기 위한 코드집합
 *
 *
 * client error (400번대 에러)
 * 400 - bad request
 * 404 - not found
 * 409 - conflict
 *
 *
 * server error(500번대 에러)
 * 500
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    //bad request
    BAD_REQUEST("해당 잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    MEMBER_INVALID_REQUEST("잘못된 회원 정보 요청입니다.", HttpStatus.BAD_REQUEST),



    //401 unauthorized
    UNAUTHORIZED("해당 요청에 대한 안증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_ORDER_UNAUTHORIZED("해당 주문에 대한 회원 권한이 없습니다.", HttpStatus.UNAUTHORIZED),


    //403 forbidden
    FORBIDDEN("해당 요청 불가합니다.", HttpStatus.FORBIDDEN),



    //not found
    NOT_FOUND("요청한 객체를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MEMBER_NOT_FOUND("요청한 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("요청한 권한을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MEMBER_ROLE_NOT_FOUND("요청한 회원 역할을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("요청한 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BOOK_NOT_FOUND("요청한 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BOOK_ISBN_NOT_FOUND("요청한 도서의 isbn에 해당하는 책을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND("해당 주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),


    //conflict
    ALREADY_DELETED("요청 객체가 이미 삭제 되었습니다.", HttpStatus.CONFLICT),
    MEMBER_ALREADY_DELETED("이미 회원 탈퇴한 회원입니다.", HttpStatus.CONFLICT),
    MEMBER_ALREADY_EXISTS("이미 존재하는 회원입니다.", HttpStatus.CONFLICT),
    MEMBER_NICKNAME_ALREADY_EXISTS("이미 사용하고 있는 닉네임입니다.", HttpStatus.CONFLICT),
    MEMBER_EMAIL_ADDRESS_ALREADY_EXISTS("이미 사용하고 있는 이메일 주소입니다.", HttpStatus.CONFLICT),
    MEMBER_PHONE_NUMBER_ALREADY_EXISTS("이미 사용하고 있는 전화번호입니다.", HttpStatus.CONFLICT),
    PRODUCT_ALREADY_EXISTS("이미 존재하는 상품입니다.", HttpStatus.CONFLICT),
    PRODUCT_BOOK_ISBN_ALREADY_EXISTS("이미 존재하는 도서 isbn 상품입니다.", HttpStatus.CONFLICT),

    CART_OVERLAPPING_EXISTS("이미 카트에 담긴 상품입니다.", HttpStatus.CONFLICT),


    //internal server error
    INTERNAL_SERVER_ERROR("서버에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR)

    ;
    private final String name;
    private final HttpStatus responseStatus;


}
