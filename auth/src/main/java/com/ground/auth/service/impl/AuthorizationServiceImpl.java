package com.ground.auth.service.impl;

import com.ground.auth.jwt.JwtProvider;
import com.yalooStore.security_utils.dto.AuthorizationResponseDto;
import com.ground.auth.service.inter.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final JwtProvider provider;

    @Override
    public AuthorizationResponseDto authorization(String token) {
        provider.isValidToken(token);

        Authentication authentication = provider.getAuthentication(token);

        List<String> authority = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return new AuthorizationResponseDto(authentication.getName(), authority);


    }
}
