package com.seungjoon.algo.auth.jwt;

import com.seungjoon.algo.exception.ExceptionResponse;
import com.seungjoon.algo.exception.MissingJwtTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.seungjoon.algo.exception.ExceptionCode.EXPIRED_JWT_TOKEN;
import static com.seungjoon.algo.exception.ExceptionCode.INVALID_JWT_TOKEN;

public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (MissingJwtTokenException e) {
            sendErrorResponse(response, new ExceptionResponse(e.getCode(), e.getMessage()));
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
