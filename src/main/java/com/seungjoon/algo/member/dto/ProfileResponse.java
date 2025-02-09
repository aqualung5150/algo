package com.seungjoon.algo.member.dto;

import com.seungjoon.algo.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponse {

    private Long id;
    private String email;
    private String username;
    private String imageUrl;

    public static ProfileResponse from(Member member) {
        return new ProfileResponse(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getImageUrl()
        );
    }
}
