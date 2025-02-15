package com.seungjoon.algo.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public abstract class CookieUtil {

    public static Optional<Cookie> getCookieFromRequest(HttpServletRequest request, String cookieName) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie
                        .getName()
                        .equals(cookieName)
                ).findFirst();
    }
}
