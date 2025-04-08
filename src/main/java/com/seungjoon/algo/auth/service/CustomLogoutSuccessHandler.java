package com.seungjoon.algo.auth.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Value("${root-domain}")
    private String rootDomain;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // access_token 삭제
        Cookie accessToken = new Cookie("access_token", null);
        accessToken.setPath("/");
        accessToken.setMaxAge(0);
        accessToken.setDomain(rootDomain);
        accessToken.setSecure(true);
        accessToken.setHttpOnly(true);
        response.addCookie(accessToken);

        // refresh_token 삭제
        Cookie refreshToken = new Cookie("refresh_token", null);
        refreshToken.setPath("/");
        refreshToken.setMaxAge(0);
        refreshToken.setDomain(rootDomain);
        refreshToken.setSecure(true);
        refreshToken.setHttpOnly(true);
        response.addCookie(refreshToken);

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
