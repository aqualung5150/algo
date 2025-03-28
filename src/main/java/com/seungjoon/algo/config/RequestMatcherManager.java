package com.seungjoon.algo.config;

import com.seungjoon.algo.member.domain.Role;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Objects;

import static com.seungjoon.algo.member.domain.Role.*;
import static org.springframework.http.HttpMethod.*;

public class RequestMatcherManager {

    public RequestMatcher getRequestMatchersByRole(@Nullable Role role) {
        return new OrRequestMatcher(REQUEST_INFO_LIST.stream()
                .filter(reqInfo -> Objects.equals(reqInfo.minRole, role))
                .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(), reqInfo.method().name()))
                .toArray(AntPathRequestMatcher[]::new));
    }

    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(
//            req(GET, "/**", null),
            //test
            req(GET, "/hello", null),
            req(GET, "/test-mem", MEMBER),

            //
            req(HEAD, "/recruit-posts/*/applicants/*", null),
            req(GET, "/members/*", null),
            req(GET, "/members/me", MEMBER),
            req(GET, "/members/*/studies", MEMBER),
            req(GET, "/members/*/recruit-posts", null),
            req(PATCH, "members/*", MEMBER),
            req(DELETE, "/**", MEMBER),
//            req(GET, "/users/*", MEMBER),

            //auth
            req(PATCH, "/auth/set-username", USERNAME_UNSET),
            req(POST, "/auth/reissue", null),
            req(POST, "/auth/signup/**", null),
//            req(POST, "/auth/login", null),
            req(POST, "/login", null),

            req(GET, "/recruit-posts/**", null),
            req(POST, "/recruit-posts/**", MEMBER),
            req(PUT, "/recruit-posts/**", MEMBER),

            //admin
            req(GET, "/admin/**", ADMIN),

            //study
            req(GET, "/study/**", MEMBER),
            req(POST, "/study/**", MEMBER),

            //submission
            req(GET, "/submissions", null),
            req(GET, "/submissions/*", null),
            req(GET, "/submissions/*/evaluations", MEMBER),

            //image
            req(POST, "/image", MEMBER)
    );

    private record RequestInfo(HttpMethod method, String pattern, Role minRole) {
    }

    private static RequestInfo req(HttpMethod method, String pattern, Role minRole) {
        return new RequestInfo(method, pattern, minRole);
    }
}
