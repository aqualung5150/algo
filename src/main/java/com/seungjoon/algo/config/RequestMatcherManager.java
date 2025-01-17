package com.seungjoon.algo.config;

import com.seungjoon.algo.user.domain.Role;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.seungjoon.algo.user.domain.Role.*;
import static org.springframework.http.HttpMethod.*;

@Component
public class RequestMatcherManager {

    public RequestMatcher getRequestMatchersByRole(@Nullable Role minRole) {
        return new OrRequestMatcher(REQUEST_INFO_LIST.stream()
                .filter(reqInfo -> Objects.equals(reqInfo.minRole, minRole))
                .map(reqInfo -> new AntPathRequestMatcher(reqInfo.pattern(), reqInfo.method().name()))
                .toArray(AntPathRequestMatcher[]::new));
    }

    // 길이 문제 상 일부만 작성
    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(
            //test
            req(GET, "/hello", null),
            req(GET, "/test-mem", MEMBER),

            //oauth2
            req(PATCH, "/oauth2/set-username", USERNAME_UNSET)
    );

    private record RequestInfo(HttpMethod method, String pattern, Role minRole) {
    }

    private static RequestInfo req(HttpMethod method, String pattern, Role minRole) {
        return new RequestInfo(method, pattern, minRole);
    }
}
