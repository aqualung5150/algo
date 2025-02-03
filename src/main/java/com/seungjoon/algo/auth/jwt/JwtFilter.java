package com.seungjoon.algo.auth.jwt;

import com.seungjoon.algo.auth.PrincipalDto;
import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.config.RequestMatcherManager;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.MissingJwtTokenException;
import com.seungjoon.algo.utils.CookieUtil;
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
import java.util.Optional;

import static com.seungjoon.algo.auth.jwt.JwtType.ACCESS;
import static com.seungjoon.algo.member.domain.Role.*;

//@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RequestMatcherManager requestMatcherManager;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !requestMatcherManager.getRequestMatchersByRole(USERNAME_UNSET).matches(request) &&
                !requestMatcherManager.getRequestMatchersByRole(MEMBER).matches(request) &&
                !requestMatcherManager.getRequestMatchersByRole(ADMIN).matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Optional<Cookie> accessTokenCookie = CookieUtil.getCookieFromRequest(request, "access_token");

        if (accessTokenCookie.isEmpty()) {
            throw new MissingJwtTokenException(ExceptionCode.MISSING_JWT_TOKEN);
        }

        String accessToken = accessTokenCookie.get().getValue();

        Long id = jwtProvider.getId(ACCESS, accessToken);
        String role = jwtProvider.getRole(ACCESS, accessToken);

        PrincipalDto principal = PrincipalDto.builder()
                .id(id)
                .role(role)
                .build();

        PrincipalDetails userDetails = new PrincipalDetails(principal);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
