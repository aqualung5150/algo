package com.seungjoon.algo.auth.controller;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.auth.dto.EmailValidationRequest;
import com.seungjoon.algo.auth.dto.SignUpRequest;
import com.seungjoon.algo.auth.jwt.JwtProvider;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameResponse;
import com.seungjoon.algo.auth.service.AuthService;
import com.seungjoon.algo.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.seungjoon.algo.auth.jwt.JwtType.ACCESS;
import static com.seungjoon.algo.auth.jwt.JwtType.REFRESH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.access-expire}")
    private Long accessExpire;
    @Value("${jwt.refresh-expire}")
    private Long refreshExpire;

    private static final String REDIRECT_URL = "redirectUrl";

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PatchMapping("set-username")
    public ResponseEntity<SetUsernameResponse> setUsername(
            @AuthenticationPrincipal PrincipalDetails principal,
            @Valid @RequestBody SetUsernameRequest setUsernameRequest,
           HttpServletRequest request,
           HttpServletResponse response
    ) {
        Member member = authService.setUsername(principal.getId(), setUsernameRequest);

        //토큰 발급
        String accessToken = jwtProvider.generateToken(ACCESS, member.getId(), member.getRole().name(), accessExpire);
        String refreshToken = jwtProvider.generateToken(REFRESH, member.getId(), member.getRole().name(), refreshExpire);
        response.addCookie(jwtProvider.createJwtCookie("access_token", accessToken));
        response.addCookie(jwtProvider.createJwtCookie("refresh_token", refreshToken));

        //redirectUrl
        String redirectUrl = request.getSession().getAttribute(REDIRECT_URL).toString();
        request.getSession().removeAttribute(REDIRECT_URL);

        return ResponseEntity.ok(SetUsernameResponse.of(redirectUrl, member));
    }

    @PostMapping("reissue")
    public ResponseEntity<Void> reissue(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        String accessToken = authService.reissue(refreshToken);

        response.addCookie(jwtProvider.createJwtCookie("access_token", accessToken));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("signup")
    public Map<String, String> signup(@Valid @RequestBody SignUpRequest request) {

        authService.signUp(request);
        return Map.of("message", "new user created");
    }

    @PostMapping("signup/available")
    public ResponseEntity<Void> validateEmail(@Valid @RequestBody EmailValidationRequest request) {

        authService.validateEmail(request);
        return ResponseEntity.noContent().build();
    }
}
