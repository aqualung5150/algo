package com.seungjoon.algo.auth.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.auth.dto.LoginRequest;
import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.auth.jwt.JwtProvider;
import com.seungjoon.algo.auth.oauth.OAuth2UserService;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.auth.service.AuthService;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.MissingJwtTokenException;
import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.user.dto.UserResponse;
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

import static com.seungjoon.algo.auth.jwt.JwtType.ACCESS;
import static com.seungjoon.algo.auth.jwt.JwtType.REFRESH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final OAuth2UserService oAuth2UserService;
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

    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody SignUpRequest request) {

        User user = authService.signUp(request);
        return new UserResponse(user);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        User user = authService.login(request);

        String accessToken = jwtProvider.generateToken(ACCESS, user.getId(), user.getRole().name(), 10 * 60 * 1000L);
        String refreshToken = jwtProvider.generateToken(REFRESH, user.getId(), user.getRole().name(), 10 * 60 * 1000L);

        response.addCookie(jwtProvider.createJwtCookie("access_token", accessToken));
        response.addCookie(jwtProvider.createJwtCookie("refresh_token", refreshToken));

        return new UserResponse(user);
    }
}
