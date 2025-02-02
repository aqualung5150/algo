package com.seungjoon.algo.auth.oauth;

import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.ExceptionResponse;
import com.seungjoon.algo.exception.ExistingAuthTypeException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof ExistingAuthTypeException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ExceptionResponse(e.getCode(), e.getMessage()).toJson());
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ExceptionResponse(ExceptionCode.LOGIN_FAILED.getCode(), ExceptionCode.LOGIN_FAILED.getMessage()).toJson());
    }
}
