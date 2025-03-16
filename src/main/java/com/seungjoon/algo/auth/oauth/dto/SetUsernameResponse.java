package com.seungjoon.algo.auth.oauth.dto;

import com.seungjoon.algo.member.domain.Member;
import com.seungjoon.algo.member.dto.ProfileResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SetUsernameResponse {

    private String redirectUrl;
    private ProfileResponse profile;

    public static SetUsernameResponse of(String redirectUrl, Member member) {
        return new SetUsernameResponse(redirectUrl, ProfileResponse.from(member));
    }
}
