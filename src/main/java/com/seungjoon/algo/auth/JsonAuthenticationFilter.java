package com.seungjoon.algo.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seungjoon.algo.auth.dto.LoginRequest;
import com.seungjoon.algo.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        if (!request.getContentType().contains("application/json")) {
            throw new AuthenticationServiceException("Authentication content-type not supported: ");
        }

        storeSessionRedirectUrl(request);

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        LoginRequest loginRequest = objectMapper.readValue(body, LoginRequest.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        return this.getAuthenticationManager().authenticate(authToken);
    }

    private void storeSessionRedirectUrl(HttpServletRequest request) {
        Cookie redirectCookie = CookieUtil.getCookieFromRequest(request, "redirectUrl").orElse(null);
        String redirectUrl = redirectCookie == null ? "" : redirectCookie.getValue();
        request.getSession().setAttribute("redirectUrl", redirectUrl);
    }
}
