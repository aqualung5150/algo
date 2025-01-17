package com.seungjoon.algo.auth.oauth;

import com.seungjoon.algo.auth.JwtProvider;
import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.user.domain.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final CustomOAuth2UserService oAuth2UserService;
    private final JwtProvider jwtProvider;

    @PatchMapping("/oauth2/set-username")
    public Map<String, String> setUsername(@AuthenticationPrincipal PrincipalDetails principal, @Valid @RequestBody SetUsernameRequest request, HttpServletResponse response) {

        User user = oAuth2UserService.setUsername(principal.getId(), request);

        String token = jwtProvider.generateToken(user.getId(), user.getRole().name(), 10 * 60 * 1000L);
        response.addCookie(jwtProvider.createJwtCookie(token));

        return Map.of("message", "new token generated");
    }
}
