package com.ground.auth.member.service.impl;

import com.ground.auth.config.ServerMetaDataConfig;
import com.ground.auth.member.service.inter.MemberLoginHistoryService;
import com.yalooStore.common_utils.dto.ResponseDto;
import com.ground.auth.member.dto.MemberLoginHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@Service
public class MemberLoginHistoryServiceImpl implements MemberLoginHistoryService {

    private final RestTemplate restTemplate;
    private final ServerMetaDataConfig serverMetaDataConfig;

    @Override
    public MemberLoginHistoryResponse saveLoginHistory(String loginId) {
        URI uri = UriComponentsBuilder.newInstance()
                .fromUriString(serverMetaDataConfig.getShopUrl())
                .pathSegment("api", "service", "members","add", "loginHistory", loginId)
                .build().toUri();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<ResponseDto<MemberLoginHistoryResponse>> response = restTemplate.exchange(
                uri, HttpMethod.POST, entity, new ParameterizedTypeReference<ResponseDto<MemberLoginHistoryResponse>>() {
                }
        );

        return response.getBody().getData();

    }
}
