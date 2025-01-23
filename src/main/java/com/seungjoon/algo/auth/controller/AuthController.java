package com.seungjoon.algo.auth.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.auth.jwt.JwtProvider;
import com.seungjoon.algo.auth.oauth.OAuth2UserService;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.auth.service.AuthService;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.MissingJwtTokenException;
import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Map<String, String> setUsername(
            @AuthenticationPrincipal PrincipalDetails principal,
            @Valid @RequestBody SetUsernameRequest setUsernameRequest,
           HttpServletRequest request,
           HttpServletResponse response
    ) {

        User user = oAuth2UserService.setUsername(principal.getId(), setUsernameRequest);

        //redirectUrl 세션 삭제
        request.getSession().removeAttribute("redirectUrl");

        //토큰 발급
        String token = jwtProvider.generateToken(ACCESS, user.getId(), user.getRole().name(), 10 * 60 * 1000L);
        response.addCookie(jwtProvider.createJwtCookie("access_token", token));

        return Map.of("message", "new token generated");
    }

    @PostMapping("/reissue")
    public Map<String, String> reissue(
            @AuthenticationPrincipal PrincipalDetails principal,
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        if (refreshToken == null) {
            throw new MissingJwtTokenException(ExceptionCode.MISSING_JWT_TOKEN);
        }

        Long id = jwtProvider.getId(REFRESH, refreshToken);
        String role = jwtProvider.getRole(REFRESH, refreshToken);

        String accessToken = jwtProvider.generateToken(ACCESS, id, role, 10 * 60 * 1000L);

        response.addCookie(jwtProvider.createJwtCookie("access_token", accessToken));

        return Map.of("message", "token reissued");
    }

    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody SignUpRequest signUpRequest) {

        User user = authService.signUp(signUpRequest);
        return new UserResponse(user);
    }
}
