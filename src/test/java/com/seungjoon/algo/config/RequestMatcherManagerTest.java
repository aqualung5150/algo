package com.seungjoon.algo.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static com.seungjoon.algo.member.domain.Role.ADMIN;
import static com.seungjoon.algo.member.domain.Role.MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestMatcherManagerTest {

    RequestMatcherManager manager = new RequestMatcherManager();

    @Test
    void getByMember() {
        //given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/test-mem");
        when(request.getPathInfo()).thenReturn(null);

        //when
        RequestMatcher requestMatcher =  manager.getRequestMatchersByRole(MEMBER);

        //then
        assertThat(requestMatcher.matches(request)).isTrue();
    }

    @Test
    void postByMember() {
        //given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/test-mem");
        when(request.getPathInfo()).thenReturn(null);

        //when
        RequestMatcher requestMatcher =  manager.getRequestMatchersByRole(MEMBER);

        //then
        assertThat(requestMatcher.matches(request)).isFalse();
    }

    @Test
    void getByAdmin() {
        //given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/admin/test");
        when(request.getPathInfo()).thenReturn(null);
        //when
        RequestMatcher requestMatcher =  manager.getRequestMatchersByRole(ADMIN);
        //then
        assertThat(requestMatcher.matches(request)).isTrue();
    }

    @Test
    void getByAnonymous() {
        //given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/hello");
        when(request.getPathInfo()).thenReturn(null);
        //when
        RequestMatcher requestMatcher =  manager.getRequestMatchersByRole(null);
        //then
        assertThat(requestMatcher.matches(request)).isTrue();
    }
}