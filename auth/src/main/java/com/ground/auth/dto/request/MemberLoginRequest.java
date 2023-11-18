package com.ground.auth.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginRequest {
    private String loginId;
    private String password;
}