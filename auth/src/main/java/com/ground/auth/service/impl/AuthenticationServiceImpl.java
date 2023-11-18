package com.ground.auth.service.impl;


import com.ground.auth.utils.AuthUtil;
import com.ground.auth.service.inter.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * 인증과 관련된 토큰 재발급, 로그아웃 작업을 진행할 때 해당하는 redis에 저장된 정보를 수정, 삭제, 추가 하는 작업을 진행하는 서비스 구현체
 * */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * {@inheritDoc}
     * */
    @Override
    public String getLoginId(String uuid) {
        return Objects.requireNonNull(redisTemplate.opsForHash().get(uuid, AuthUtil.LOGIN_ID.getValue())).toString();
    }

    @Override
    public String getRoles(String uuid) {
        return Objects.requireNonNull(redisTemplate.opsForHash().get(uuid, AuthUtil.PRINCIPAL.getValue()).toString());
    }


    /**
     * {@inheritDoc}
     * */
    @Override
    public void doReissue(String uuid, String accessToken) {

        redisTemplate.opsForHash().delete(uuid, AuthUtil.ACCESS_TOKEN.getValue());
        redisTemplate.opsForHash().put(uuid, AuthUtil.ACCESS_TOKEN.getValue(), accessToken);

    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void doLogout(String uuid) {

        redisTemplate.opsForHash().delete(uuid, AuthUtil.ACCESS_TOKEN.getValue());
        redisTemplate.opsForHash().delete(uuid, AuthUtil.REFRESH_TOKEN.getValue());
        redisTemplate.opsForHash().delete(uuid, AuthUtil.LOGIN_ID.getValue());
        redisTemplate.opsForHash().delete(uuid, AuthUtil.PRINCIPAL.getValue());

    }
}
