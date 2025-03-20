package com.seungjoon.algo.auth.oauth;

import com.seungjoon.algo.auth.PrincipalDetails;
import com.seungjoon.algo.auth.PrincipalDto;
import com.seungjoon.algo.exception.ExceptionCode;
import com.seungjoon.algo.exception.ExistingAuthTypeException;
import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.domain.MemberState;
import com.seungjoon.algo.member.domain.Role;
import com.seungjoon.algo.member.repository.MemberRepository;
import com.seungjoon.algo.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.seungjoon.algo.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final HttpServletRequest request;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        storeSessionRedirectUrl();

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;
        if (registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        } else {
            return null;
        }

        Member member = saveOrUpdateUser(oAuth2UserInfo);

        PrincipalDto principal = PrincipalDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(null)
                .name(oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId())
                .role(member.getRole().name())
                .build();

        return new PrincipalDetails(principal);
    }

    private Member saveOrUpdateUser(OAuth2UserInfo oAuth2UserInfo) {
        Member exist = memberRepository.findByEmail(oAuth2UserInfo.getEmail())
                .orElse(null);

        if (exist != null) {
            if (!exist.getAuthType().equals(oAuth2UserInfo.getProvider())) {
                ExceptionCode exceptionCode = getOtherAuthTypeExceptionCode(exist.getAuthType());
                throw new ExistingAuthTypeException(exceptionCode);
            }

            return exist;
        }

        return memberRepository.save(
                Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .username(UUID.randomUUID().toString())
                .authType(oAuth2UserInfo.getProvider())
                .role(Role.USERNAME_UNSET)
                .state(MemberState.ACTIVE)
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .build()
        );
    }

    private ExceptionCode getOtherAuthTypeExceptionCode(String authType) {

        ExceptionCode exceptionCode = null;
        if (authType.equals("google")) {
            exceptionCode = EXISTING_GOOGLE_MEMBER;
        } else if (authType.equals("naver")) {
            exceptionCode = EXISTING_NAVER_MEMBER;
        } else {
            exceptionCode = EXISTING_NORMAL_MEMBER;
        }

        return exceptionCode;
    }

    private void storeSessionRedirectUrl() {
        Cookie redirectCookie = CookieUtil.getCookieFromRequest(request, "redirectUrl").orElse(null);
        String redirectUrl = redirectCookie == null ? "" : redirectCookie.getValue();
        request.getSession().setAttribute("redirectUrl", redirectUrl);
    }
}
