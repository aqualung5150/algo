package com.seungjoon.algo.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class PrincipalDetails implements OAuth2User, UserDetails {

    private final PrincipalDto principal;

    @Override
    public String getPassword() {
        return principal.password();
    }

    @Override
    public String getUsername() {
        return principal.username();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(principal::role);

        return authorities;
    }

    @Override
    public String getName() {
        return principal.name();
    }

    public Long getId() {
        return principal.id();
    }
}
