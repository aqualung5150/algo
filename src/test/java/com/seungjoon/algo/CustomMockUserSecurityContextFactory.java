package com.seungjoon.algo;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.auth.PrincipalDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class CustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithMockMember> {
    @Override
    public SecurityContext createSecurityContext(WithMockMember annotation) {

        PrincipalDto principalDto = PrincipalDto.builder()
                .id(annotation.id())
                .role(annotation.role())
                .password("1234")
                .name("test-name")
                .username("test-username")
                .build();

        PrincipalDetails userDetails = new PrincipalDetails(principalDto);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}
