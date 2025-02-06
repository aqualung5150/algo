package com.seungjoon.algo.member.dto;

import com.seungjoon.algo.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private Long id;
    private String email;
    private String username;
    private String imageUrl;
    private String authType;
    private String role;
    private String state;

    public static MemberResponse from(Member member) {

        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getImageUrl(),
                member.getAuthType(),
                member.getRole().name(),
                member.getState().name()
        );
    }
}
