package com.seungjoon.algo.auth.jwt;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import static com.seungjoon.algo.auth.jwt.JwtType.ACCESS;
import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
class JwtProviderTest {

//    @Autowired JwtProvider jwtProvider;
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
}