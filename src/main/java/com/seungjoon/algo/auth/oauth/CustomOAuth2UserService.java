package com.seungjoon.algo.auth.oauth;

import com.seungjoon.algo.auth.oauth.dto.SetUsernameRequest;
import com.seungjoon.algo.exception.BadRequestException;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.user.domain.Role;
import com.seungjoon.algo.user.domain.User;
import com.seungjoon.algo.user.domain.UserState;
import com.seungjoon.algo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;
        if (registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else {
            return null;
        }

        User user = saveOrUpdateUser(oAuth2UserInfo);

        PrincipalDTO principal = PrincipalDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(null)
                .name(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId())
                .role(user.getRole().name())
                .build();

        return new PrincipalDetails(principal);
    }

    private User saveOrUpdateUser(OAuth2UserInfo oAuth2UserInfo) {
        User exist = userRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElse(null);

        if (exist != null) {
            exist.changeImageUrl(oAuth2UserInfo.getImageUrl());
            return exist;
        }

        return userRepository.save(
                User.builder()
                .email(oAuth2UserInfo.getEmail())
                .username(UUID.randomUUID().toString())
                .role(Role.USERNAME_UNSET)
                .state(UserState.ACTIVE)
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .build()
        );
    }

    public User setUsername(Long userId, SetUsernameRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException(ExceptionCode.NOT_FOUND_USER));

        user.changeUsername(request.getUsername());
        user.changeRole(Role.MEMBER);

        return user;
    }
}
