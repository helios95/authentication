package com.ground.auth.dto.response;


import lombok.*;

import java.util.List;


/**
 * 인증, 인가 관련에서 로그인시에 필요한 회원 정보를 담은 dto 객체입니다.
 * */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginResponse {


    private Long memberId;
    private String loginId;
    private String name;
    private String nickname;
    private String email;
    private String password;
    private List<String> roles;



}
