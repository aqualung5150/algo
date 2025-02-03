package com.seungjoon.algo.member.dto;

import com.seungjoon.algo.member.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponse {

    Long id;
    String email;
    String username;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
    }
}
