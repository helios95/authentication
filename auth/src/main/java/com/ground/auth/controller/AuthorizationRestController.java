package com.ground.auth.controller;


import com.ground.auth.service.inter.AuthorizationService;
import com.yalooStore.common_utils.dto.ResponseDto;
import com.yalooStore.security_utils.dto.AuthorizationResponseDto;
import com.ground.auth.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorizations")
@RequiredArgsConstructor
public class AuthorizationRestController {
    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthorizationService authorizationService;


    @GetMapping(headers = "Authorization")
    public ResponseDto<AuthorizationResponseDto> authorization(@RequestHeader(name = "Authorization") String authorization) {
        AuthorizationResponseDto authorizationMeta = authorizationService.authorization(
                removeBearerPrefix(authorization));

        return ResponseDto.<AuthorizationResponseDto>builder()
                .success(true)
                .status(HttpStatus.OK)
                .data(authorizationMeta)
                .build();
    }

    private String removeBearerPrefix(String authorization) {

        if(!authorization.startsWith(BEARER_PREFIX)){
            throw new InvalidTokenException();
        }
        return authorization.substring(BEARER_PREFIX.length());
    }


}
