package com.seungjoon.algo.auth;

import com.seungjoon.algo.auth.oauth.PrincipalDTO;
import com.seungjoon.algo.auth.oauth.PrincipalDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Cookie accessTokenCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("access_token")).findFirst().orElse(null);

        if (accessTokenCookie == null) {

            //TODO - no token exception
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = accessTokenCookie.getValue();
        if (jwtProvider.isExpired(accessToken)) {

            //TODO - expired exception
            filterChain.doFilter(request, response);
            return;
        }


        Long id = jwtProvider.getId(accessToken);
        String role = jwtProvider.getRole(accessToken);

        PrincipalDTO principal = PrincipalDTO.builder()
                .id(id)
                .role(role)
                .build();

        PrincipalDetails userDetails = new PrincipalDetails(principal);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
