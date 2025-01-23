package com.seungjoon.algo.auth.oauth;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.auth.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.seungjoon.algo.auth.jwt.JwtType.ACCESS;
import static com.seungjoon.algo.auth.jwt.JwtType.REFRESH;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REDIRECT_URL = "redirectUrl";

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 로그인 성공 시 브라우저 쿠키 삭제
        Cookie cookie = new Cookie(REDIRECT_URL, null);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();

        String role = authentication.getAuthorities()
                .iterator().next()
                .getAuthority();

        String accessToken = jwtProvider.generateToken(ACCESS, userDetails.getId(), role, 10 * 60 * 1000L);
        String refreshToken = jwtProvider.generateToken(REFRESH, userDetails.getId(), role, 10 * 60 * 1000L);

        response.addCookie(jwtProvider.createJwtCookie("access_token", accessToken));
        response.addCookie(jwtProvider.createJwtCookie("refresh_token", refreshToken));

        if (role.equals("USERNAME_UNSET")) {
            response.sendRedirect("http://localhost:5173/set-username");
        } else {
            response.sendRedirect(request.getSession().getAttribute(REDIRECT_URL).toString());
            request.getSession().removeAttribute(REDIRECT_URL);
        }

    }
}
