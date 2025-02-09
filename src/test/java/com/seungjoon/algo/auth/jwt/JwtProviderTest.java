package com.seungjoon.algo.auth.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import static com.seungjoon.algo.auth.jwt.JwtType.ACCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    JwtProvider jwtProvider = new JwtProvider();

    @Test
    void getIdAndRole() {
        //given
        String token = jwtProvider.generateToken(ACCESS, 10L, "TEST", 10 * 60 * 1000L);
        //when
        Long id = jwtProvider.getId(ACCESS, token);
        String role = jwtProvider.getRole(ACCESS, token);
        //then
        assertThat(id).isEqualTo(10L);
        assertThat(role).isEqualTo("TEST");
    }

    @Test
    void createJwtCookie() {
        //given
        String token = jwtProvider.generateToken(ACCESS, 10L, "TEST", 10 * 60 * 1000L);
        //when
        Cookie accessToken = jwtProvider.createJwtCookie("access_token", token);
        //then
        assertThat(accessToken.getName()).isEqualTo("access_token");
        assertThat(accessToken.getValue()).isEqualTo(token);
        assertThat(accessToken.getPath()).isEqualTo("/");
    }

    @Test
    void getFromInvalidToken() {
        //given
        String invalid = "invalid";
        String wrongToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        //when

        //then
        assertThatThrownBy(() -> jwtProvider.getId(ACCESS,invalid)).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> jwtProvider.getId(ACCESS, wrongToken)).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> jwtProvider.getRole(ACCESS,invalid)).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> jwtProvider.getRole(ACCESS, wrongToken)).isInstanceOf(JwtException.class);
    }
}