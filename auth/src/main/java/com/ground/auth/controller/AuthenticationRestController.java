package com.ground.auth.controller;


import com.ground.auth.jwt.JwtProvider;
import com.ground.auth.service.inter.AuthenticationService;
import com.ground.auth.utils.AuthUtil;
import com.yalooStore.common_utils.dto.ResponseDto;
import com.ground.auth.exception.InvalidAuthorizationHeaderException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthenticationService authenticationService;



    @PostMapping("/reissue")
    public ResponseDto<Void> tokenReissue(HttpServletRequest request, HttpServletResponse response){
        String accessToken = request.getHeader(AUTHORIZATION);
        String uuid = request.getHeader(AuthUtil.HEADER_UUID.getValue());

        if(isValidHeader(accessToken,uuid)){
            throw new InvalidAuthorizationHeaderException();
        }
        //해당 회원이 가지고 있는 고유 uuid가 올바르지 않은 값일 때 response
        if (isNotValidKey(uuid)){
            return ResponseDto.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .success(false)
                    .errorMessages(List.of("유효하지 않은 회원입니다."))
                    .build();
        }

        if(isValidRefreshToken(uuid)) {
            return ResponseDto.<Void>builder()
                    .success(false)
                    .status(HttpStatus.BAD_REQUEST)
                    .errorMessages(List.of("refresh token 만료!"))
                    .build();
        }

        String loginId = authenticationService.getLoginId(uuid);
        String role = authenticationService.getRoles(uuid);

        List<String> roles = extractMemberRoles(role);

        String reissueToken = jwtProvider.reissueToken(loginId, roles);
        authenticationService.doReissue(uuid, reissueToken);

        long expiredTime = jwtProvider.extractExpiredTime(reissueToken).getTime();

        response.addHeader(AUTHORIZATION, "Bearer" + reissueToken);
        response.addHeader(AuthUtil.HEADER_UUID.getValue(), uuid);
        response.addHeader(AuthUtil.HEADER_EXPIRED_TIME.getValue(), String.valueOf(expiredTime));

        return ResponseDto.<Void>builder()
                .success(true)
                .status(HttpStatus.OK)
                .build();

    }


    /**
     * 레디스에 모든 회원 권한이 String으로 저장된 형태를 ArrayList로 변환해서 돌려주기 위해서 사용하는 메소드
     * */
    private List<String> extractMemberRoles(String role) {
        return Arrays.asList(role.replaceAll("[\\[\\]]", "").split(", "));
    }

    /**
     * 해당 키값에 해당하는 정보가 없다면 유효하지 않은 키로 판단
     * @return 올바르지 않은 key = true 올바른 키 = false
     * */
    private boolean isNotValidKey(String uuid) {
        return redisTemplate.opsForHash().keys(uuid).isEmpty();
    }

    /**
     * 토큰 재발급에 사용되는 토큰이 만료시간이 넘지 않은 경우라면 재발급
     * */
    private boolean isValidRefreshToken(String uuid) {

        String refreshToken= Objects.requireNonNull(redisTemplate.opsForHash().get(uuid, AuthUtil.REFRESH_TOKEN.getValue())).toString();

        long expiredTime = jwtProvider.extractExpiredTime(refreshToken).getTime();
        long now = new Date().getTime();

        return (expiredTime - (now/1000)) > 0;
    }


    private boolean isValidHeader(String accessToken, String uuid) {
        return Objects.isNull(accessToken) || Objects.isNull(uuid)
                || !accessToken.startsWith("Bearer ") || !jwtProvider.isValidToken(accessToken.substring(7));
    }
}
