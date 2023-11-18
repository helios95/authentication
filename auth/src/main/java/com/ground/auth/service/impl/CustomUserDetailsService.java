package com.ground.auth.service.impl;

import com.ground.auth.config.ServerMetaDataConfig;
import com.ground.auth.dto.response.MemberLoginResponse;
import com.yalooStore.common_utils.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;



@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final RestTemplate restTemplate;
    private final ServerMetaDataConfig serverMetaDataConfig;


    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(headers);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(serverMetaDataConfig
                .getShopUrl()).pathSegment("api", "service", "members", "login", loginId).build();

        try {
            ResponseEntity<ResponseDto<MemberLoginResponse>> memberLoginResponse = restTemplate
                    .exchange(uri.toUri(),
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<>() {
                            });
            MemberLoginResponse data = memberLoginResponse.getBody().getData();
            User user = new User(data.getLoginId(), data.getPassword(), getAuthorities(data));
            return user;

        } catch (HttpClientErrorException e){
            throw new UsernameNotFoundException("not found username");
        }
    }

    private List<? extends GrantedAuthority> getAuthorities(MemberLoginResponse data) {
        List<SimpleGrantedAuthority> authorities = data.getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return authorities;
    }

}
