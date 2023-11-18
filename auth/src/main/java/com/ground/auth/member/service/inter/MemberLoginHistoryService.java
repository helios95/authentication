package com.ground.auth.member.service.inter;

import com.ground.auth.member.dto.MemberLoginHistoryResponse;

public interface MemberLoginHistoryService {

    MemberLoginHistoryResponse saveLoginHistory(String loginId);
}
