package com.seungjoon.algo.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    public static Optional<Cookie> getCookieFromRequest(HttpServletRequest request, String cookieName) {

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie
                        .getName()
                        .equals(cookieName)
                ).findFirst();
    }
}
