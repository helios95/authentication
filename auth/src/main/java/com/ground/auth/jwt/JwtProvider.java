package com.ground.auth.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * jwt 토큰 관련 클래스
 *
 * 발행, 재발행 등의 작업을 진행후 Jwt 정보 리턴
 * */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final long ACCESS_TOKEN_EXPIRED_TIME = 1000L * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_EXPIRED_TIME = 1000L * 60L * 60L * 24L * 7L; // 7일

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;


    private Key getSecretKey() {
        byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String loginId, List<String> roles, long expiredTime){

        Claims claims = Jwts.claims().setSubject(loginId);
        claims.put("roles", roles);
        Date date = new Date();

        Key secretKey = getSecretKey();

        String jwt = Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + expiredTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        return jwt;
    }

    public String createAccessToken(String loginId, List<String> roles){
        return createToken(loginId, roles, ACCESS_TOKEN_EXPIRED_TIME);
    }

    public String createRefreshToken(String loginId, List<String> roles){
        return createToken(loginId, roles, REFRESH_TOKEN_EXPIRED_TIME);

    }

    public String extractLoginId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

    }

    public Date extractExpiredTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }


    public boolean isValidToken(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 엑세스 토큰 재발급
     * */
    public String reissueToken(String loginId, List<String> roles) {
        String accessToken = createAccessToken(loginId, roles);
        return accessToken;
    }

    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(extractLoginId(token));
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}
