package com.seungjoon.algo.auth;

import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.ExceptionResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.seungjoon.algo.exception.ExceptionCode.*;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, new ExceptionResponse(EXPIRED_JWT_TOKEN.getCode(), EXPIRED_JWT_TOKEN.getMessage()));
        } catch (JwtException e) {
            sendErrorResponse(response, new ExceptionResponse(INVALID_JWT_TOKEN.getCode(), INVALID_JWT_TOKEN.getMessage()));
        }
    }

    private void sendErrorResponse(HttpServletResponse response, ExceptionResponse exceptionResponse) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(exceptionResponse.toJson());
    }
}
