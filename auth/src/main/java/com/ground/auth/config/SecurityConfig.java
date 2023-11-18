package com.ground.auth.config;


import com.ground.auth.jwt.JwtProvider;
import com.ground.auth.member.service.inter.MemberLoginHistoryService;
import com.ground.auth.filter.JwtAuthenticationFilter;
import com.ground.auth.handler.JwtFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.client.RestTemplate;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RestTemplate restTemplate;
    private final ServerMetaDataConfig serverMetaDataConfig;

    private final MemberLoginHistoryService memberLoginHistoryService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests().anyRequest().permitAll();
        http.formLogin().disable();
        http.logout().disable();
        http.csrf().disable();
        http.addFilter(jwtAuthenticationFilter());

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().sameOrigin();

        return http.build();

    }

    private AbstractAuthenticationProcessingFilter jwtAuthenticationFilter() throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager(null),
                memberLoginHistoryService, jwtProvider, redisTemplate);
        jwtAuthenticationFilter.setAuthenticationFailureHandler(jwtFailureHandler());
        jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");
        return jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationFailureHandler jwtFailureHandler() {
        return new JwtFailureHandler(serverMetaDataConfig);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
