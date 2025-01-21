package com.seungjoon.algo.auth.oauth;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.auth.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.seungjoon.algo.auth.jwt.JwtType.*;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2 User
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
            //TODO - originalURL - api응답을 주고 프론트에서 처리해야 할까? - No 리다이렉트를 반드시 해야함.
            //TODO - USERNAME_UNSET일 때 /auth/set-username으로 리디렉션
            response.sendRedirect("http://localhost:5173/");
        }

    }
}
