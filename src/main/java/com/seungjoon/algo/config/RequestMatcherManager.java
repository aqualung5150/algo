package com.seungjoon.algo.config;

import com.seungjoon.algo.user.domain.Role;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Objects;

import static com.seungjoon.algo.user.domain.Role.*;
import static org.springframework.http.HttpMethod.*;

public class RequestMatcherManager {

    public RequestMatcher getRequestMatchersByRole(@Nullable Role minRole) {
        return new OrRequestMatcher(REQUEST_INFO_LIST.stream()
                .filter(reqInfo -> Objects.equals(reqInfo.minRole, minRole))
                .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(), reqInfo.method().name()))
                .toArray(AntPathRequestMatcher[]::new));
    }

    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(
            //test
            req(GET, "/hello", null),
            req(GET, "/test-mem", MEMBER),

            //
            req(GET, "/**", null),

            //auth
            req(PATCH, "/auth/set-username", USERNAME_UNSET),
            req(POST, "/auth/reissue", null),
            req(POST, "/auth/signup", null),
            req(POST, "/auth/login", null),
            req(POST, "/login", null),

            //admin
            req(GET, "/admin/**", ADMIN)
    );

    private record RequestInfo(HttpMethod method, String pattern, Role minRole) {
    }

    private static RequestInfo req(HttpMethod method, String pattern, Role minRole) {
        return new RequestInfo(method, pattern, minRole);
    }
}
