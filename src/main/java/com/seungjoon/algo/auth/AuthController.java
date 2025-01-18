package com.seungjoon.algo.auth;

import com.seungjoon.algo.auth.oauth.CustomOAuth2UserService;
import com.seungjoon.algo.auth.oauth.PrincipalDetails;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.MissingJwtTokenException;
import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.util.Map;
import java.util.Optional;

import static com.seungjoon.algo.auth.oauth.JwtType.ACCESS;
import static com.seungjoon.algo.auth.oauth.JwtType.REFRESH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final CustomOAuth2UserService oAuth2UserService;
    private final JwtProvider jwtProvider;

    @PatchMapping("/set-username")
    public Map<String, String> setUsername(@AuthenticationPrincipal PrincipalDetails principal, @Valid @RequestBody SetUsernameRequest request, HttpServletResponse response) {

        User user = oAuth2UserService.setUsername(principal.getId(), request);

        String token = jwtProvider.generateToken(ACCESS, user.getId(), user.getRole().name(), 10 * 60 * 1000L);
        response.addCookie(jwtProvider.createJwtCookie("access_token", token));

        return Map.of("message", "new token generated");
    }

    @PostMapping("/reissue")
    public Map<String, String> reissue(@AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieFromRequest(request, "refresh_token");

        if (refreshTokenCookie.isEmpty()) {
            throw new MissingJwtTokenException(ExceptionCode.MISSING_JWT_TOKEN);
        }

        String refreshToken = refreshTokenCookie.get().getValue();

        Long id = jwtProvider.getId(REFRESH, refreshToken);
        String role = jwtProvider.getRole(REFRESH, refreshToken);

        String accessToken = jwtProvider.generateToken(ACCESS, id, role, 10 * 60 * 1000L);

        response.addCookie(jwtProvider.createJwtCookie("access_token", accessToken));

        return Map.of("message", "token reissued");
    }
}
