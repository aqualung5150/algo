package com.seungjoon.algo.auth.oauth;

import com.seungjoon.algo.user.domain.Role;
import com.seungjoon.algo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final OAuth2UserInfo oAuth2UserInfo;

    //리소스 서버마다 달라서 사용하지 않음
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

//        authorities.add(() -> "ROLE_" + user.getRole());

        return authorities;
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();
    }

    public Long getId() {
        return user.getId();
    }
}
