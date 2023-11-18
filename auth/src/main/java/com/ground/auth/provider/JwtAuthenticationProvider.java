//package com.yaloostore.auth.provider;
//
//import com.yaloostore.auth.exception.UserPasswordNotMatchesException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.util.Objects;
//
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthenticationProvider implements AuthenticationProvider {
//    private final UserDetailsService userDetailsService;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//
//        String loginId = authentication.getName();
//        String password = authentication.getCredentials().toString();
//
//        UserDetails user = userDetailsService.loadUserByUsername(loginId);
//
//        if (!this.passwordEncoder.matches(password, user.getPassword())){
//            log.info("password not matches");
//            throw new UserPasswordNotMatchesException("password is not match");
//        }
//
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(user.getUsername(),
//                        "",
//                        user.getAuthorities());
//
//        return authenticationToken;
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return true;
//    }
//}
