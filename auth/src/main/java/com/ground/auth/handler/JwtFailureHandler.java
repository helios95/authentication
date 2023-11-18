package com.ground.auth.handler;

import com.ground.auth.config.ServerMetaDataConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;


@RequiredArgsConstructor
public class JwtFailureHandler implements AuthenticationFailureHandler {

    private final ServerMetaDataConfig serverMetaDataConfig;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.sendRedirect(serverMetaDataConfig.getFrontUrl() + "/members/login");
    }
}
