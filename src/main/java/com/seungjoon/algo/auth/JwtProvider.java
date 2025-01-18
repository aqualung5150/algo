package com.seungjoon.algo.auth;

import com.seungjoon.algo.auth.oauth.JwtType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

import static com.seungjoon.algo.auth.oauth.JwtType.*;

@Component
public class JwtProvider {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public JwtProvider() {
        this. accessKey = new SecretKeySpec(KeyGenerators.secureRandom(32).generateKey(), Jwts.SIG.HS256.key().build().getAlgorithm());
        this. refreshKey = new SecretKeySpec(KeyGenerators.secureRandom(32).generateKey(), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getId(JwtType type, String token) {
        return parseClaims(type, token).get("id", Long.class);
    }

    public String getRole(JwtType type, String token) {
        return parseClaims(type, token).get("role", String.class);
    }

    public boolean isExpired(JwtType type, String token) {
        return parseClaims(type, token).getExpiration().before(new Date());
    }

    private Claims parseClaims(JwtType type, String token) {
        SecretKey secretKey = getSecretKey(type);
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    private SecretKey getSecretKey(JwtType type) {

        if (type == ACCESS) {
            return this.accessKey;
        }
        if (type == REFRESH) {
            return this.refreshKey;
        }
        return null;
    }

    public String generateToken(JwtType type, Long id, String role, Long expiredMs) {
        SecretKey secretKey = getSecretKey(type);
        return Jwts.builder()
                .claim("id", id)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public Cookie createJwtCookie(String key, String token) {
        Cookie cookie = new Cookie(key, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
//        cookie.setMaxAge(10 * 60);
//        cookie.setSecure(true);

        return cookie;
    }
}
