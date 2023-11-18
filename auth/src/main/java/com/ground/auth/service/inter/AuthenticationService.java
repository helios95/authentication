package com.ground.auth.service.inter;


/**
 * 인증과 관련된 서비스 로직 처리 클래스
 *
 * 예를 들면 redis(in-memory 기반의 데이터베이스)를 사용하고 있으니 해당 db에서 정보 획득
 * */
public interface AuthenticationService {

    /**
     * uuid로 유저 Login id 조회
     *
     * @param uuid 해당 회원의 고유 uuid
     * @return 회원 로그인 아이디값
     * */
    String getLoginId(String uuid);


    /**
     * uuid로 해당 회원의 권한 조회
     *
     * @param uuid 해당 회원의 고유 uuid
     * @return 회원 권한
     * */
    String getRoles(String uuid);


    /**
     * uuid로 accessToken 삭제 후 재발급 accessToken 저장
     *
     * @param uuid 회원 고유 uuid
     * @param accessToken 새롭게 발급 받은 accessToken
     * */
    void doReissue(String uuid, String accessToken);

    /**
     * uuid로 jwt 토큰 삭제
     *
     * @param uuid 회원 고유 uuid
     * */
    void doLogout(String uuid);
}
